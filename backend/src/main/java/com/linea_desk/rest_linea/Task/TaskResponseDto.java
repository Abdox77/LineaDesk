package com.linea_desk.rest_linea.Task;



public class TaskResponseDto {
    private Long id;
    private String taskName;
    private Long projectId;
    private String description;
    private Task.TASK_STATE state;
    private Task.TASK_IMPORTANCE importance;

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
        this.state = task.getTaskState();
        this.importance = task.getTaskImportance();
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
    
    public Task.TASK_IMPORTANCE getImportance() { return importance; }
    public void setImportance(Task.TASK_IMPORTANCE importance) { this.importance = importance; }
}
