package com.linea_desk.rest_linea.Project;

import com.linea_desk.rest_linea.Project.Project.PROJECT_STATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProjectRequestDto {
    private Integer sessions;

    @NotNull(message = "Project name cannot be null")
    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 3, message = "Project name must be at least 3 characters long")
    private String projectName;

    private String description;
    private String githubLink;
    private PROJECT_STATE state;

    public ProjectRequestDto() { }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
    
    public Integer getSessions() { return this.sessions; }
    public void setSessions(Integer sessions) { this.sessions = sessions; }

    public PROJECT_STATE getState() { return state; }
    public void setState(PROJECT_STATE state) { this.state = state; }
}
