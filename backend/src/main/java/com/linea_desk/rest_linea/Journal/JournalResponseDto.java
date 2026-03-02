package com.linea_desk.rest_linea.Journal;

import java.util.Collection;
import java.util.Collections;

import com.linea_desk.rest_linea.Journal.Journal.JOURNAL_VISIBILITY;
import com.linea_desk.rest_linea.Page.PageResponseDto;

public class JournalResponseDto {
    private Long id;
    private String name;
    private JOURNAL_VISIBILITY visibility;
    private Collection<PageResponseDto> pages;

    public JournalResponseDto() { }

    public JournalResponseDto(Journal journal) {
        this.id = journal.getJournalId();
        this.name = journal.getName();
        this.visibility = journal.getVisibility();

        if (journal.getPages() == null) {
            this.pages = Collections.emptyList();
        } else {
            this.pages = journal.getPages().stream()
                    .map(PageResponseDto::new)
                    .toList();
        }
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JOURNAL_VISIBILITY getVisibility() { return visibility; }
    public void setVisibility(JOURNAL_VISIBILITY visibility) { this.visibility = visibility; }

    public Collection<PageResponseDto> getPages() { return pages; }
    public void setPages(Collection<PageResponseDto> pages) { this.pages = pages; }
}

