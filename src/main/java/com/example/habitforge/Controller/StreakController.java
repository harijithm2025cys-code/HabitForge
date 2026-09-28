package com.example.habitforge.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.habitforge.Dto.CalendarDayResponse;
import com.example.habitforge.Dto.StreakResponse;
import com.example.habitforge.Service.CalendarService;
import com.example.habitforge.Service.StreakService;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin
public class StreakController {

    private final StreakService streakService;
    private final CalendarService calendarService;

    public StreakController(StreakService streakService, CalendarService calendarService) {
        this.streakService = streakService;
        this.calendarService = calendarService;
    }

    @GetMapping("/{habitId}/streak")
    public ResponseEntity<StreakResponse> getStreak(@PathVariable Long habitId) {
        StreakResponse response = streakService.getStreak(habitId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{habitId}/calendar")
    public ResponseEntity<List<CalendarDayResponse>> getCalendar(
            @PathVariable Long habitId,
            @RequestParam int year,
            @RequestParam int month) {
        List<CalendarDayResponse> response = calendarService.getMonthlyCalendar(habitId, year, month);
        return ResponseEntity.ok(response);
    }
}
