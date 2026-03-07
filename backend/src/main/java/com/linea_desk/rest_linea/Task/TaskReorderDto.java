package com.linea_desk.rest_linea.Task;

public class TaskReorderDto {
    private Long id;
    private Integer sortOrder;

    public TaskReorderDto() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}

