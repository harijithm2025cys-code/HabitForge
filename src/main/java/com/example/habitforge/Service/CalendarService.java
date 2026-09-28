package com.example.habitforge.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.habitforge.Dto.CalendarDayResponse;
import com.example.habitforge.Exception.HabitNotFoundException;
import com.example.habitforge.Model.CompletionLog;
import com.example.habitforge.Model.Habit;
import com.example.habitforge.Repo.CompletionLogRepository;
import com.example.habitforge.Repo.HabitRepository;

@Service
public class CalendarService {

    private final HabitRepository habitRepository;
    private final CompletionLogRepository completionLogRepository;
    private final StreakService streakService;

    public CalendarService(HabitRepository habitRepository,
                           CompletionLogRepository completionLogRepository,
                           StreakService streakService) {
        this.habitRepository = habitRepository;
        this.completionLogRepository = completionLogRepository;
        this.streakService = streakService;
    }

    @Transactional(readOnly = true)
    public List<CalendarDayResponse> getMonthlyCalendar(Long habitId, int year, int month) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException(habitId));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<CompletionLog> logs = completionLogRepository.findByHabitAndCompletionDateBetween(habit, startDate, endDate);
        Set<LocalDate> completedDates = new HashSet<>();
        for (CompletionLog log : logs) {
            completedDates.add(log.getCompletionDate());
        }

        List<CalendarDayResponse> calendarDays = new ArrayList<>();
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            boolean required = streakService.isRequiredDay(habit, date);
            boolean completed = completedDates.contains(date);

            calendarDays.add(new CalendarDayResponse(date, required, completed));
        }

        return calendarDays;
    }
}
