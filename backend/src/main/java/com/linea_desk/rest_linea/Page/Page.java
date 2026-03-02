package com.linea_desk.rest_linea.Page;

import java.time.LocalDateTime;

import com.linea_desk.rest_linea.Journal.Journal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="Pages")
public class Page {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(length=255, nullable=false)
    @NotNull(message="Page title cannot be Null")
    @NotBlank(message="Page title cannot be Blank")
    @Size(min = 1, message = "Page title must be at least 1 character long")
    private String title;

    @Column(columnDefinition="TEXT")
    private String content;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(nullable=false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name="journal_id")
    private Journal journal;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Page() { }

    public Page(String title) {
        this.title = title;
    }

    public Page(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Long getPageId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Journal getJournal() { return journal; }
    public void setJournal(Journal journal) {
        if (journal != null) {
            this.journal = journal;
        }
    }
}

