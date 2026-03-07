package com.linea_desk.rest_linea.Page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PageRequestDto {
    @NotNull(message = "Page title cannot be null")
    @NotBlank(message = "Page title cannot be blank")
    @Size(min = 1, message = "Page title must not be empty")
    private String title;

    private String content;

    @NotNull(message = "Journal ID is required")
    private Long journalId;

    public PageRequestDto() { }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getJournalId() { return journalId; }
    public void setJournalId(Long journalId) { this.journalId = journalId; }
}
