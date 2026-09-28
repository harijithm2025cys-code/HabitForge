package com.example.habitforge.Service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.habitforge.Dto.CompletionRequest;
import com.example.habitforge.Dto.StreakResponse;
import com.example.habitforge.Exception.CompletionAlreadyExistsException;
import com.example.habitforge.Exception.HabitNotFoundException;
import com.example.habitforge.Exception.InvalidCompletionException;
import com.example.habitforge.Model.CompletionLog;
import com.example.habitforge.Model.Habit;
import com.example.habitforge.Repo.CompletionLogRepository;
import com.example.habitforge.Repo.HabitRepository;

@Service
public class CompletionService {

    private static final Logger logger = LoggerFactory.getLogger(CompletionService.class);

    private final HabitRepository habitRepository;
    private final CompletionLogRepository completionLogRepository;
    private final StreakService streakService;

    public CompletionService(HabitRepository habitRepository,
                             CompletionLogRepository completionLogRepository,
                             StreakService streakService) {
        this.habitRepository = habitRepository;
        this.completionLogRepository = completionLogRepository;
        this.streakService = streakService;
    }

    @Transactional
    public StreakResponse completeHabit(Long habitId, CompletionRequest request) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException(habitId));

        LocalDate completionDate = (request != null && request.getCompletionDate() != null)
                ? request.getCompletionDate()
                : LocalDate.now();

        if (completionDate.isAfter(LocalDate.now())) {
            throw new InvalidCompletionException("Completion date cannot be in the future");
        }

        if (!streakService.isRequiredDay(habit, completionDate)) {
            throw new InvalidCompletionException("Habit is not scheduled for this date (not a required day)");
        }

        if (completionLogRepository.findByHabitAndCompletionDate(habit, completionDate).isPresent()) {
            throw new CompletionAlreadyExistsException("Habit is already completed for this date");
        }

        CompletionLog log = new CompletionLog(null, habit, completionDate);
        completionLogRepository.save(log);

        streakService.recalculateStreaks(habit);
        StreakResponse streakResponse = streakService.getStreak(habit.getId());

        logger.info("[NOTIFICATION] Habit '{}' marked DONE for {}. Current Streak: {} days, Best Streak: {} days",
                habit.getName(), completionDate, streakResponse.getCurrentStreak(), streakResponse.getBestStreak());

        return streakResponse;
    }
}
