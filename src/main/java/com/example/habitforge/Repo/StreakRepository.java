package com.example.habitforge.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.habitforge.Model.Habit;
import com.example.habitforge.Model.Streak;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {

    Optional<Streak> findByHabit(Habit habit);

    void deleteByHabit(Habit habit);
}
