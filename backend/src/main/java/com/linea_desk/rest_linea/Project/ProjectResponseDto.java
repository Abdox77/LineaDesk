package com.linea_desk.rest_linea.Project;

public class ProjectResponseDto {
    private Long id;
    private String projectName;
    private String description;
    private String githubLink;

    public ProjectResponseDto() { }

    public ProjectResponseDto(Project project) {
        this.id = project.getProjectId();
        this.projectName = project.getProjectName();
        this.description = project.getDescription();
        this.githubLink = project.getGithubLink();
    }

    public Long getProjectId() { return id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
}
