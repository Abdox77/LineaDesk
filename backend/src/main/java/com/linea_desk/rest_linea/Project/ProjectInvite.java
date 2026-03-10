package com.linea_desk.rest_linea.Project;

import java.time.LocalDateTime;
import java.util.UUID;

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

@Entity
@Table(name = "Project_Invites")
public class ProjectInvite {

    public enum INVITE_STATUS {
        PENDING,
        ACCEPTED,
        EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(32) default 'PENDING'")
    private INVITE_STATUS status = INVITE_STATUS.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public ProjectInvite() {}

    public ProjectInvite(Project project) {
        this.project = project;
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(7);
        this.status = INVITE_STATUS.PENDING;
    }

    public Long getId() { return id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public INVITE_STATUS getStatus() { return status; }
    public void setStatus(INVITE_STATUS status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}

