package com.linea_desk.rest_linea.Habit;

import java.time.LocalDate;

public class HabitLogResponseDto {
    private Long id;
    private Long habitId;
    private LocalDate date;
    private boolean completed;

    public HabitLogResponseDto() {}

    public HabitLogResponseDto(HabitLog log) {
        this.id = log.getId();
        this.habitId = log.getHabit().getHabitId();
        this.date = log.getDate();
        this.completed = log.isCompleted();
    }

    public Long getId() { return id; }
    public Long getHabitId() { return habitId; }
    public LocalDate getDate() { return date; }
    public boolean isCompleted() { return completed; }
}

