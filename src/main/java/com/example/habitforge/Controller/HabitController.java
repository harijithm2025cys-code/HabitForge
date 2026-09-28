package com.example.habitforge.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.habitforge.Dto.CreateHabitRequest;
import com.example.habitforge.Model.Habit;
import com.example.habitforge.Service.HabitService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody CreateHabitRequest request) {
        Habit created = habitService.createHabit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits() {
        return ResponseEntity.ok(habitService.getAllHabits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHabitById(@PathVariable Long id) {
        Habit habit = habitService.getHabitById(id);
        if (habit == null) {
            return ResponseEntity.ok("Data not found");
        }
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@PathVariable Long id, @Valid @RequestBody CreateHabitRequest request) {
        return ResponseEntity.ok(habitService.updateHabit(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }
}
