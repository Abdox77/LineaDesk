package com.linea_desk.rest_linea.Task;


import java.time.LocalDate;

import com.linea_desk.rest_linea.Task.Task.TASK_STATE;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskRequestDto {
    private int id;

    @Min(value = 0, message = "Duration must be a non-negative integer")
    private int duration;

    @NotNull(message="Task name cannot be Null")
    @NotBlank(message="Task name cannot be Blank")
    @Size(min = 3, message = "Task name must be at least 3 characters long")
    private String taskName;


    private Long projectId;
    private String description;
    private TASK_STATE state;
    private Task.TASK_IMPORTANCE importance;
    private Integer sortOrder;
    private LocalDate dueDate;
    private Long parentTaskId;


    public TaskRequestDto() { }

    public int getId() { return id; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TASK_STATE getState() { return state; }
    public void setState(TASK_STATE state) { this.state = state; }

    public Task.TASK_IMPORTANCE getImportance() { return importance; }
    public void setImportance(Task.TASK_IMPORTANCE importance) { this.importance = importance; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Long getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Long parentTaskId) { this.parentTaskId = parentTaskId; }
}
