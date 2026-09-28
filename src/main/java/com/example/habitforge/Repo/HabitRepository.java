package com.example.habitforge.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.habitforge.Model.Habit;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
}
