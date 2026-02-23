package com.linea_desk.rest_linea.Task;

import com.linea_desk.rest_linea.Project.Project;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="Tasks")
public class Task
{
    public enum TASK_STATE {
        PENDING,
        IN_PROGRESS,
        FINISHED
    };

    public enum TASK_IMPORTANCE {
        NORMAL,
        MEDIUM,
        IMPORTANT,
        CRUCIAL
    };

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(length=255, nullable=false)
    @NotNull(message="Task name cannot be Null")
    @NotBlank(message="Task name cannot be Blank")
    @Size(min = 3, message = "Task name must be at least be 3 characters long")
    private String taskName;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    @Column
    @Min(0)
    private int duration = 0;   

    @Column(length=255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="varchar(255) default 'NORMAL'")
    private TASK_IMPORTANCE importance;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="varchar(255) default 'PENDING'")
    private TASK_STATE state;

    public Task() { }

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Long getTaskId() { return id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { 
        if(taskName != null && !taskName.trim().isEmpty()) { 
            this.taskName = taskName;
        }
    }

    public int getTaskDuration() { return duration; }
    public void setTaskDuration(int duration) { this.duration = duration; }

    public TASK_IMPORTANCE getTaskImportance() { return importance; }
    public void setTaskImportance(TASK_IMPORTANCE importance) { this.importance = importance; }

    public TASK_STATE getTaskState() { return state; }
    public void setTaskState(TASK_STATE state) { this.state = state; }

    public String getTaskDescription() { return description; }
    public void setTaskDescription(String description) { 
        if (description != null && !description.trim().isEmpty()) {
            this.description = description;
        }
    }

    public Project getProject() { return project; }
    public void setProject(Project project) { 
        if (project != null) {
            this.project = project;
        }
    }
}
