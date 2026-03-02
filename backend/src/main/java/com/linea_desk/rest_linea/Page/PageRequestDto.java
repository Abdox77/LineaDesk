package com.linea_desk.rest_linea.Page;

public class PageRequestDto {
    private String title;
    private String content;
    private Long journalId;

    public PageRequestDto() { }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getJournalId() { return journalId; }
    public void setJournalId(Long journalId) { this.journalId = journalId; }
}

