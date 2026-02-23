package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.Collections;

import com.linea_desk.rest_linea.Task.TaskResponseDto;

public class ProjectResponseDto {
    private Long id;
    private String projectName;
    private String description;
    private String githubLink;
    private Integer sessions;
    private Collection<TaskResponseDto> tasks;

    public ProjectResponseDto() { }

    public ProjectResponseDto(Project project) {
        this.id = project.getProjectId();
        this.projectName = project.getProjectName();
        this.description = project.getDescription();

        if(project.getGithubLink() == null) {
            this.githubLink = "";
        }
        else {
            this.githubLink = project.getGithubLink();
        }

        if (project.getSessions() == null) {
            this.sessions = 0;
        }
        else {
            this.sessions = project.getSessions();
        }

        if (project.getTasks() == null) {
            this.tasks = Collections.emptyList();
        }
        else {
            this.tasks = project.getTasks().stream()
                    .map(TaskResponseDto::new)
                    .toList();
        }
    }


    public Long getProjectId() { return id; }

    public Integer getSessions() { return this.sessions; }
    public void setSessions(Integer sessions) { this.sessions = sessions; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }

    public Collection<TaskResponseDto> getTasks() { return tasks; }
    public void setTasks(Collection<TaskResponseDto> tasks) { this.tasks = tasks; }
}
