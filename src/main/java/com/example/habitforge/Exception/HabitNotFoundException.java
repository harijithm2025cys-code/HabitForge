package com.example.habitforge.Exception;

public class HabitNotFoundException extends RuntimeException {

    public HabitNotFoundException(String message) {
        super(message);
    }

    public HabitNotFoundException(Long id) {
        super("Habit not found with id: " + id);
    }
}
