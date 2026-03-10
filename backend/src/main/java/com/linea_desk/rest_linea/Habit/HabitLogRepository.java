package com.linea_desk.rest_linea.Habit;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findByHabitIdAndDateBetween(Long habitId, LocalDate from, LocalDate to);
    List<HabitLog> findByHabitIdInAndDateBetween(List<Long> habitIds, LocalDate from, LocalDate to);
    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);
    void deleteByHabitIdAndDate(Long habitId, LocalDate date);
    long countByHabitIdAndDateBetween(Long habitId, LocalDate from, LocalDate to);
}

