package com.linea_desk.rest_linea.Habit;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.DuplicateResourceException;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;

@Service
public class HabitServices {
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;

    public HabitServices(HabitRepository habitRepository, HabitLogRepository habitLogRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
    }

    @Transactional
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

    @Transactional(readOnly = true)
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

    public HabitLogResponseDto logHabit(Long habitId, LocalDate date, User user) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", habitId));
        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        if (habitLogRepository.findByHabitIdAndDate(habitId, date).isPresent()) {
            throw new DuplicateResourceException("HabitLog", "date", date.toString());
        }
        HabitLog log = new HabitLog(habit, date);
        habitLogRepository.save(log);
        return new HabitLogResponseDto(log);
    }

    @Transactional
    public void unlogHabit(Long habitId, LocalDate date, User user) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", habitId));
        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        habitLogRepository.deleteByHabitIdAndDate(habitId, date);
    }

    public List<HabitLogResponseDto> getHabitLogs(Long habitId, LocalDate from, LocalDate to, User user) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", habitId));
        if (!habit.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        return habitLogRepository.findByHabitIdAndDateBetween(habitId, from, to)
                .stream()
                .map(HabitLogResponseDto::new)
                .toList();
    }

    public List<HabitLogResponseDto> getAllHabitLogsForUser(LocalDate from, LocalDate to, User user) {
        Collection<Habit> habits = habitRepository.findAllByUser(user);
        List<Long> habitIds = habits.stream().map(Habit::getHabitId).toList();
        if (habitIds.isEmpty()) return List.of();
        return habitLogRepository.findByHabitIdInAndDateBetween(habitIds, from, to)
                .stream()
                .map(HabitLogResponseDto::new)
                .toList();
    }
}
