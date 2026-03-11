package com.linea_desk.rest_linea.Habit;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Habit_Logs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"habit_id", "date"})
})
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean completed = true;

    public HabitLog() {}

    public HabitLog(Habit habit, LocalDate date) {
        this.habit = habit;
        this.date = date;
        this.completed = true;
    }

    public Long getId() { return id; }

    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

