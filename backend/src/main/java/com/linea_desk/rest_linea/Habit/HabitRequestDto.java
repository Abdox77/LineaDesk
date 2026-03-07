package com.linea_desk.rest_linea.Habit;

import com.linea_desk.rest_linea.Habit.Habit.HABIT_TYPE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HabitRequestDto {
    @NotNull(message = "Habit name cannot be null")
    @NotBlank(message = "Habit name cannot be blank")
    @Size(min = 3, message = "Habit name must be at least 3 characters long")
    private String habitName;

    private HABIT_TYPE type;
    private Integer streaks;

    public HabitRequestDto() { }

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }

    public HABIT_TYPE getType() { return type; }
    public void setType(HABIT_TYPE type) { this.type = type; }

    public Integer getStreaks() { return streaks; }
    public void setStreaks(Integer streaks) { this.streaks = streaks; }
}
