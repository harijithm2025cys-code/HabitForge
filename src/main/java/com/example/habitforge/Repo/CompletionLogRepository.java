package com.example.habitforge.Repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.habitforge.Model.CompletionLog;
import com.example.habitforge.Model.Habit;

@Repository
public interface CompletionLogRepository extends JpaRepository<CompletionLog, Long> {

    List<CompletionLog> findByHabitOrderByCompletionDateDesc(Habit habit);

    Optional<CompletionLog> findByHabitAndCompletionDate(Habit habit, LocalDate completionDate);

    List<CompletionLog> findByHabitAndCompletionDateBetween(Habit habit, LocalDate start, LocalDate end);

    void deleteByHabit(Habit habit);
}
