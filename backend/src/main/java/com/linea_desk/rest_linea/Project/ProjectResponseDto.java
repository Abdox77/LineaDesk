package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Hibernate;

import com.linea_desk.rest_linea.Project.Project.PROJECT_STATE;
import com.linea_desk.rest_linea.Task.TaskResponseDto;

public class ProjectResponseDto {
    private Long id;
    private String projectName;
    private String description;
    private String githubLink;
    private Integer sessions;
    private PROJECT_STATE state;
    private Collection<TaskResponseDto> tasks;
    private List<ProjectMemberResponseDto> members;

    public ProjectResponseDto() { }

    public ProjectResponseDto(Project project) {
        this(project, Collections.emptyList());
    }

    public ProjectResponseDto(Project project, List<ProjectMemberResponseDto> members) {
        this.id = project.getProjectId();
        this.projectName = project.getProjectName();
        this.description = project.getDescription();
        this.state = project.getState();
        this.githubLink = project.getGithubLink() == null ? "" : project.getGithubLink();
        this.sessions = project.getSessions() == null ? 0 : project.getSessions();
        if (project.getTasks() == null || !Hibernate.isInitialized(project.getTasks())) {
            this.tasks = Collections.emptyList();
        } else {
            this.tasks = project.getTasks().stream()
                    .map(TaskResponseDto::new)
                    .toList();
        }
        this.members = members != null ? members : Collections.emptyList();
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

    public PROJECT_STATE getState() { return state; }
    public void setState(PROJECT_STATE state) { this.state = state; }

    public Collection<TaskResponseDto> getTasks() { return tasks; }
    public void setTasks(Collection<TaskResponseDto> tasks) { this.tasks = tasks; }

    public List<ProjectMemberResponseDto> getMembers() { return members; }
    public void setMembers(List<ProjectMemberResponseDto> members) { this.members = members; }
}
