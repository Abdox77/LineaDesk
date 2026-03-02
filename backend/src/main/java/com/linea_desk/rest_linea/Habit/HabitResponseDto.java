package com.linea_desk.rest_linea.Habit;

import com.linea_desk.rest_linea.Habit.Habit.HABIT_TYPE;

public class HabitResponseDto {
    private Long id;
    private String habitName;
    private HABIT_TYPE type;
    private Integer streaks;

    public HabitResponseDto() { }

    public HabitResponseDto(Habit habit) {
        this.id = habit.getHabitId();
        this.habitName = habit.getHabitName();
        this.type = habit.getType();

        if (habit.getStreaks() == null) {
            this.streaks = 0;
        } else {
            this.streaks = habit.getStreaks();
        }
    }

    public Long getId() { return id; }

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }

    public HABIT_TYPE getType() { return type; }
    public void setType(HABIT_TYPE type) { this.type = type; }

    public Integer getStreaks() { return streaks; }
    public void setStreaks(Integer streaks) { this.streaks = streaks; }
}

