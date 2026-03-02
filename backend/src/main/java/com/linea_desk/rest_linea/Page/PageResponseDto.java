package com.linea_desk.rest_linea.Page;

import java.time.LocalDateTime;

public class PageResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long journalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PageResponseDto() { }

    public PageResponseDto(Page page) {
        this.id = page.getPageId();
        this.title = page.getTitle();
        this.content = page.getContent() != null ? page.getContent() : "";
        this.journalId = page.getJournal().getJournalId();
        this.createdAt = page.getCreatedAt();
        this.updatedAt = page.getUpdatedAt();
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getJournalId() { return journalId; }
    public void setJournalId(Long journalId) { this.journalId = journalId; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
