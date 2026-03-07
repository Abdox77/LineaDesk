package com.linea_desk.rest_linea.Task;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BulkTaskStateDto {
    @NotEmpty(message = "Task IDs list cannot be empty")
    private List<Long> taskIds;

    @NotNull(message = "State is required")
    private Task.TASK_STATE state;

    public BulkTaskStateDto() { }

    public List<Long> getTaskIds() { return taskIds; }
    public void setTaskIds(List<Long> taskIds) { this.taskIds = taskIds; }

    public Task.TASK_STATE getState() { return state; }
    public void setState(Task.TASK_STATE state) { this.state = state; }
}

