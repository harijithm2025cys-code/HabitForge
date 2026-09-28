package com.example.habitforge.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.habitforge.Dto.StreakResponse;
import com.example.habitforge.Exception.HabitNotFoundException;
import com.example.habitforge.Model.CompletionLog;
import com.example.habitforge.Model.FrequencyType;
import com.example.habitforge.Model.Habit;
import com.example.habitforge.Model.Streak;
import com.example.habitforge.Repo.CompletionLogRepository;
import com.example.habitforge.Repo.HabitRepository;
import com.example.habitforge.Repo.StreakRepository;

@Service
public class StreakService {

    private static final Logger logger = LoggerFactory.getLogger(StreakService.class);

    private final HabitRepository habitRepository;
    private final CompletionLogRepository completionLogRepository;
    private final StreakRepository streakRepository;

    public StreakService(HabitRepository habitRepository,
                         CompletionLogRepository completionLogRepository,
                         StreakRepository streakRepository) {
        this.habitRepository = habitRepository;
        this.completionLogRepository = completionLogRepository;
        this.streakRepository = streakRepository;
    }

    public boolean isRequiredDay(Habit habit, LocalDate date) {
        if (habit.getFrequencyType() == FrequencyType.DAILY) {
            return true;
        }

        if (habit.getFrequencyType() == FrequencyType.SPECIFIC_DAYS) {
            String targetDays = habit.getTargetDays();
            if (targetDays == null || targetDays.trim().isEmpty()) {
                return false;
            }

            int currentDayOfWeek = date.getDayOfWeek().getValue(); // 1 = Monday, ..., 7 = Sunday
            String[] splitDays = targetDays.split(",");
            for (String dayStr : splitDays) {
                if (dayStr.trim().equals(String.valueOf(currentDayOfWeek))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Transactional
    public void recalculateStreaks(Habit habit) {
        List<CompletionLog> logs = completionLogRepository.findByHabitOrderByCompletionDateDesc(habit);
        Set<LocalDate> completedDates = new HashSet<>();
        LocalDate earliest = null;

        for (CompletionLog log : logs) {
            LocalDate date = log.getCompletionDate();
            completedDates.add(date);
            if (earliest == null || date.isBefore(earliest)) {
                earliest = date;
            }
        }

        int currentStreak = 0;
        LocalDate today = LocalDate.now();

        // 1. Calculate CURRENT STREAK
        // If today is a required day and completed, increment currentStreak
        if (isRequiredDay(habit, today) && completedDates.contains(today)) {
            currentStreak++;
        }

        // Trace backwards from yesterday
        LocalDate checkDate = today.minusDays(1);
        if (earliest != null && !checkDate.isBefore(earliest)) {
            while (!checkDate.isBefore(earliest)) {
                if (isRequiredDay(habit, checkDate)) {
                    if (completedDates.contains(checkDate)) {
                        currentStreak++;
                    } else {
                        // A required day was missed! Stop current streak.
                        break;
                    }
                }
                checkDate = checkDate.minusDays(1);
            }
        }

        // 2. Calculate BEST STREAK from history
        int calculatedBest = 0;
        if (earliest != null) {
            int tempStreak = 0;
            LocalDate d = earliest;
            while (!d.isAfter(today)) {
                if (isRequiredDay(habit, d)) {
                    if (completedDates.contains(d)) {
                        tempStreak++;
                        if (tempStreak > calculatedBest) {
                            calculatedBest = tempStreak;
                        }
                    } else {
                        // If d is strictly before today and was missed, reset temp streak
                        if (d.isBefore(today)) {
                            tempStreak = 0;
                        }
                    }
                }
                d = d.plusDays(1);
            }
        }

        // Update streak entity safely (never decrease stored bestStreak)
        Streak streak = streakRepository.findByHabit(habit).orElse(null);
        if (streak == null) {
            streak = new Streak(null, habit, 0, 0);
        }

        streak.setCurrentStreak(currentStreak);

        int best = streak.getBestStreak();
        if (currentStreak > best) {
            best = currentStreak;
        }
        if (calculatedBest > best) {
            best = calculatedBest;
        }
        streak.setBestStreak(best);

        streakRepository.save(streak);

        logger.info("[NOTIFICATION] Streak updated for '{}': Current Streak = {} days, Best Streak = {} days",
                habit.getName(), currentStreak, best);
    }

    @Transactional(readOnly = true)
    public StreakResponse getStreak(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException(habitId));

        Streak streak = streakRepository.findByHabit(habit)
                .orElseGet(() -> new Streak(null, habit, 0, 0));

        return new StreakResponse(
                habit.getId(),
                habit.getName(),
                streak.getCurrentStreak(),
                streak.getBestStreak()
        );
    }
}
