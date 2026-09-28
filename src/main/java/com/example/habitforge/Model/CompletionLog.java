package com.example.habitforge.Model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "completion_logs",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_habit_date", columnNames = {"habit_id", "completion_date"})
    }
)
public class CompletionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @NotNull
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    public CompletionLog() {
    }

    public CompletionLog(Long id, Habit habit, LocalDate completionDate) {
        this.id = id;
        this.habit = habit;
        this.completionDate = completionDate;
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

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
}
