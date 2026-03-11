package com.linea_desk.rest_linea.Project;

import com.linea_desk.rest_linea.User.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Project_Members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
public class ProjectMember {

    public enum MEMBER_ROLE {
        OWNER,
        COLLABORATOR,
        VIEWER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(32) default 'COLLABORATOR'")
    private MEMBER_ROLE role = MEMBER_ROLE.COLLABORATOR;

    public ProjectMember() {}

    public ProjectMember(Project project, User user, MEMBER_ROLE role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public Long getId() { return id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public MEMBER_ROLE getRole() { return role; }
    public void setRole(MEMBER_ROLE role) { this.role = role; }
}

