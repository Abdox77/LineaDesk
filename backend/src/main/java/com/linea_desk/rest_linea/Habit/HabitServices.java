package com.linea_desk.rest_linea.Habit;

import java.util.Collection;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.User.User;

@Log4j2
@Service
public class HabitServices {
    private final HabitRepository habitRepository;

    public HabitServices(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public Optional<HabitResponseDto> createNewHabit(HabitRequestDto req, User user) {
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
        return Optional.of(new HabitResponseDto(habit));
    }

    public Optional<HabitResponseDto> getHabitById(Long id, User user) {
        Optional<Habit> habit = habitRepository.findById(id);

        if (habit.isEmpty()) {
            return Optional.empty();
        }

        if (!habit.get().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        return Optional.of(new HabitResponseDto(habit.get()));
    }

    public Optional<Collection<HabitResponseDto>> getAllHabitsForUser(User user) {
        Collection<Habit> habits = habitRepository.findAllByUser(user);
        Collection<HabitResponseDto> responseDtos = habits.stream()
                .map(HabitResponseDto::new)
                .toList();
        return Optional.of(responseDtos);
    }

    public Optional<HabitResponseDto> updateHabit(Long id, HabitRequestDto req, User user) {
        Optional<Habit> habitOpt = habitRepository.findById(id);

        if (habitOpt.isEmpty()) {
            return Optional.empty();
        }

        Habit habit = habitOpt.get();

        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
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
        return Optional.of(new HabitResponseDto(habit));
    }

    public boolean deleteHabitById(Long id, User user) {
        Optional<Habit> habitOpt = habitRepository.findById(id);

        if (habitOpt.isEmpty()) {
            return false;
        }

        Habit habit = habitOpt.get();

        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            return false;
        }

        habitRepository.delete(habit);
        return true;
    }
}

