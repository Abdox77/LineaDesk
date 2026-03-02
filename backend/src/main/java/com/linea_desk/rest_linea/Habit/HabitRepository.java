package com.linea_desk.rest_linea.Habit;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linea_desk.rest_linea.User.User;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByHabitName(String habitName);
    Collection<Habit> findAllByUser(User user);
}

