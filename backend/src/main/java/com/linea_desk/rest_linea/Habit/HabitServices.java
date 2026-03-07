package com.linea_desk.rest_linea.Habit;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;

@Service
public class HabitServices {
    private final HabitRepository habitRepository;

    public HabitServices(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public HabitResponseDto createNewHabit(HabitRequestDto req, User user) {
        Habit habit = new Habit();
        habit.setHabitName(req.getHabitName());
        if (req.getType() != null) {
            habit.setType(req.getType());
        }
        if (req.getStreaks() != null) {
            habit.setStreaks(req.getStreaks());
        }
        habit.setUser(user);
        habitRepository.save(habit);
        return new HabitResponseDto(habit);
    }

    public HabitResponseDto getHabitById(Long id, User user) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", id));
        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        return new HabitResponseDto(habit);
    }

    public Collection<HabitResponseDto> getAllHabitsForUser(User user) {
        Collection<Habit> habits = habitRepository.findAllByUser(user);
        return habits.stream()
                .map(HabitResponseDto::new)
                .toList();
    }

    public HabitResponseDto updateHabit(Long id, HabitRequestDto req, User user) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", id));
        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        if (req.getHabitName() != null && !req.getHabitName().trim().isEmpty()) {
            habit.setHabitName(req.getHabitName());
        }
        if (req.getType() != null) {
            habit.setType(req.getType());
        }
        if (req.getStreaks() != null) {
            habit.setStreaks(req.getStreaks());
        }
        habitRepository.save(habit);
        return new HabitResponseDto(habit);
    }

    public void deleteHabitById(Long id, User user) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", id));
        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        habitRepository.delete(habit);
    }
}
