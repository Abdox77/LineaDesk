package com.linea_desk.rest_linea.Project;
import java.util.Collection;

import com.linea_desk.rest_linea.Task.Task;
import com.linea_desk.rest_linea.User.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="Projects")
public class Project {
    public enum PROJECT_STATE {
        PENDING,
        IN_PROGRESS,
        FINISHED
    };

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(length=255, nullable=false, unique=true)
    @NotNull(message="Project name cannot be Null")
    @NotBlank(message="Project name cannot be Blank")
    @Size(min = 3, message = "Project name must be at least be 3 characters long")
    private String projectName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="varchar(255) default 'PENDING'")
    private PROJECT_STATE state = PROJECT_STATE.PENDING;

    @Column(length=255)
    private String description;

    @Column(length=255)
    private String githubLink;

    @OneToMany(mappedBy="project", cascade=CascadeType.ALL)
    private Collection<Task> tasks;

    @Column(columnDefinition = "integer default 0")    
    private Integer sessions = 0;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Project() { }

    public Project(String projectName) {
        this.projectName = projectName;
    }

    public Project(String projectName, String description) {
        this.projectName = projectName;
        this.description = description;
    }

    public Long getProjectId() { return id; }

    public String getProjectName()  { return projectName; }
    public void setProjectName(String projectName)  { this.projectName = projectName; }

    public PROJECT_STATE getState() { return state; }
    public void setState(PROJECT_STATE state)  { this.state = state; }

    public User getUser() { return user; }
    public void setUser(User user) {  
        if (user != null) {
            this.user = user;
        }
    }

    public Integer getSessions() { return sessions; }
    public void setSessions(Integer sessions) { this.sessions = sessions; }

    public Collection<Task> getTasks() { return tasks; }
    public void addTask(Task task) {
        if (task != null) {
            this.tasks.add(task);
        }
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { 
        if (description != null && !description.trim().isEmpty()) {
            this.description = description;
        }
    }

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { 
        if (githubLink != null && !githubLink.trim().isEmpty()) {
            this.githubLink = githubLink;
        }
    }
}
