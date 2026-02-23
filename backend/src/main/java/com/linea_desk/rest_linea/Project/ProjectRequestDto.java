package com.linea_desk.rest_linea.Project;

public class ProjectRequestDto {
    private Integer sessions;
    private String projectName;
    private String description;
    private String githubLink;

    public ProjectRequestDto() { }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
    
    public Integer getSessions() { return this.sessions; }
    public void setSessions(Integer sessions) { this.sessions = sessions; }
}
