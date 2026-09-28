package com.example.habitforge.Dto;

public class StreakResponse {

    private Long habitId;
    private String habitName;
    private int currentStreak;
    private int bestStreak;

    public StreakResponse() {
    }

    public StreakResponse(Long habitId, String habitName, int currentStreak, int bestStreak) {
        this.habitId = habitId;
        this.habitName = habitName;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }
}
