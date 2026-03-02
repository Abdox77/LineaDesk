package com.linea_desk.rest_linea.Habit;

import com.linea_desk.rest_linea.User.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="Habits")
public class Habit {
    public enum HABIT_TYPE {
        FITNESS,
        MENTAL_WELLBEING,
        INTELLECTUAL
    };

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(length=255, nullable=false)
    @NotNull(message="Habit name cannot be Null")
    @NotBlank(message="Habit name cannot be Blank")
    @Size(min = 3, message = "Habit name must be at least 3 characters long")
    private String habitName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="varchar(255) default 'FITNESS'")
    private HABIT_TYPE type = HABIT_TYPE.FITNESS;

    @Column(columnDefinition = "integer default 0")
    private Integer streaks = 0;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Habit() { }

    public Habit(String habitName) {
        this.habitName = habitName;
    }

    public Habit(String habitName, HABIT_TYPE type) {
        this.habitName = habitName;
        this.type = type;
    }

    public Long getHabitId() { return id; }

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }

    public HABIT_TYPE getType() { return type; }
    public void setType(HABIT_TYPE type) { this.type = type; }

    public Integer getStreaks() { return streaks; }
    public void setStreaks(Integer streaks) { this.streaks = streaks; }

    public User getUser() { return user; }
    public void setUser(User user) {
        if (user != null) {
            this.user = user;
        }
    }
}

