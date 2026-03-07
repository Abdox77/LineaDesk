package com.linea_desk.rest_linea.Task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.hibernate.Hibernate;

public class TaskResponseDto {
    private Long id;
    private String taskName;
    private Long projectId;
    private String description;
    private int duration;
    private Task.TASK_STATE state;
    private Task.TASK_IMPORTANCE importance;
    private Integer sortOrder;
    private LocalDate dueDate;
    private Long parentTaskId;
    private List<TaskResponseDto> subtasks;

    public TaskResponseDto() { }

    public TaskResponseDto(Long id, String taskName, Long projectId, String description, Task.TASK_STATE state, Task.TASK_IMPORTANCE importance) {
        this.id = id;
        this.taskName = taskName;
        this.projectId = projectId;
        this.description = description;
        this.state = state;
        this.importance = importance;
    }

    public TaskResponseDto(Task task) {
        this.id = task.getTaskId();
        this.taskName = task.getTaskName();
        this.projectId = task.getProject().getProjectId();
        this.description = task.getTaskDescription();
        this.duration = task.getTaskDuration();
        this.state = task.getTaskState();
        this.importance = task.getTaskImportance();
        this.sortOrder = task.getSortOrder();
        this.dueDate = task.getDueDate();
        this.parentTaskId = (Hibernate.isInitialized(task.getParentTask()) && task.getParentTask() != null)
                ? task.getParentTask().getTaskId() : null;

        if (Hibernate.isInitialized(task.getSubtasks()) && task.getSubtasks() != null && !task.getSubtasks().isEmpty()) {
            this.subtasks = task.getSubtasks().stream()
                    .map(subtask -> {
                        TaskResponseDto dto = new TaskResponseDto();
                        dto.setId(subtask.getTaskId());
                        dto.setTaskName(subtask.getTaskName());
                        dto.setProjectId(subtask.getProject().getProjectId());
                        dto.setDescription(subtask.getTaskDescription());
                        dto.setDuration(subtask.getTaskDuration());
                        dto.setState(subtask.getTaskState());
                        dto.setImportance(subtask.getTaskImportance());
                        dto.setSortOrder(subtask.getSortOrder());
                        dto.setDueDate(subtask.getDueDate());
                        dto.setSubtasks(Collections.emptyList());
                        return dto;
                    })
                    .toList();
        } else {
            this.subtasks = Collections.emptyList();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Task.TASK_STATE getState() { return state; }
    public void setState(Task.TASK_STATE state) { this.state = state; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public Task.TASK_IMPORTANCE getImportance() { return importance; }
    public void setImportance(Task.TASK_IMPORTANCE importance) { this.importance = importance; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Long getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Long parentTaskId) { this.parentTaskId = parentTaskId; }

    public List<TaskResponseDto> getSubtasks() { return subtasks; }
    public void setSubtasks(List<TaskResponseDto> subtasks) { this.subtasks = subtasks; }
}
