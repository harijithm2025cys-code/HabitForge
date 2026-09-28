package com.example.habitforge.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "streaks")
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(optional = false)
    @JoinColumn(name = "habit_id", unique = true, nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private int currentStreak = 0;

    @Column(nullable = false)
    private int bestStreak = 0;

    public Streak() {
    }

    public Streak(Long id, Habit habit, int currentStreak, int bestStreak) {
        this.id = id;
        this.habit = habit;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
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
