package com.example.habitforge.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.habitforge.Dto.CreateHabitRequest;
import com.example.habitforge.Exception.HabitNotFoundException;
import com.example.habitforge.Model.FrequencyType;
import com.example.habitforge.Model.Habit;
import com.example.habitforge.Model.Streak;
import com.example.habitforge.Repo.CompletionLogRepository;
import com.example.habitforge.Repo.HabitRepository;
import com.example.habitforge.Repo.StreakRepository;

@Service
public class HabitService {

    private static final Logger logger = LoggerFactory.getLogger(HabitService.class);

    private final HabitRepository habitRepository;
    private final StreakRepository streakRepository;
    private final CompletionLogRepository completionLogRepository;
    private final StreakService streakService;

    public HabitService(HabitRepository habitRepository,
                        StreakRepository streakRepository,
                        CompletionLogRepository completionLogRepository,
                        StreakService streakService) {
        this.habitRepository = habitRepository;
        this.streakRepository = streakRepository;
        this.completionLogRepository = completionLogRepository;
        this.streakService = streakService;
    }

    @Transactional
    public Habit createHabit(CreateHabitRequest request) {
        if (request.getFrequencyType() == FrequencyType.SPECIFIC_DAYS) {
            if (request.getTargetDays() == null || request.getTargetDays().trim().isEmpty()) {
                throw new IllegalArgumentException("Target days must be provided for SPECIFIC_DAYS frequency");
            }
        }

        Habit habit = new Habit();
        habit.setName(request.getName().trim());
        habit.setDescription(request.getDescription());
        habit.setFrequencyType(request.getFrequencyType());
        habit.setTargetDays(request.getTargetDays());

        Habit savedHabit = habitRepository.save(habit);

        Streak streak = new Streak(null, savedHabit, 0, 0);
        streakRepository.save(streak);

        logger.info("[NOTIFICATION] Created new habit '{}' (ID: {}) with frequency {}",
                savedHabit.getName(), savedHabit.getId(), savedHabit.getFrequencyType());

        return savedHabit;
    }

    @Transactional(readOnly = true)
    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Habit getHabitById(Long id) {
        return habitRepository.findById(id).orElse(null);
    }

    @Transactional
    public Habit updateHabit(Long id, CreateHabitRequest request) {
        Habit habit = getHabitById(id);
        if (habit == null) {
            throw new HabitNotFoundException(id);
        }

        if (request.getFrequencyType() == FrequencyType.SPECIFIC_DAYS) {
            if (request.getTargetDays() == null || request.getTargetDays().trim().isEmpty()) {
                throw new IllegalArgumentException("Target days must be provided for SPECIFIC_DAYS frequency");
            }
        }

        habit.setName(request.getName().trim());
        habit.setDescription(request.getDescription());
        habit.setFrequencyType(request.getFrequencyType());
        habit.setTargetDays(request.getTargetDays());

        Habit updatedHabit = habitRepository.save(habit);
        streakService.recalculateStreaks(updatedHabit);

        logger.info("[NOTIFICATION] Updated habit '{}' (ID: {})", updatedHabit.getName(), updatedHabit.getId());

        return updatedHabit;
    }

    @Transactional
    public void deleteHabit(Long id) {
        Habit habit = getHabitById(id);
        if (habit == null) {
            return;
        }

        streakRepository.deleteByHabit(habit);
        completionLogRepository.deleteByHabit(habit);
        habitRepository.delete(habit);

        logger.info("[NOTIFICATION] Deleted habit '{}' (ID: {}) along with its streak and logs",
                habit.getName(), habit.getId());
    }
}
