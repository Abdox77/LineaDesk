package com.linea_desk.rest_linea.Journal;

import com.linea_desk.rest_linea.Journal.Journal.JOURNAL_VISIBILITY;

public class JournalRequestDto {
    private String name;
    private JOURNAL_VISIBILITY visibility;

    public JournalRequestDto() { }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JOURNAL_VISIBILITY getVisibility() { return visibility; }
    public void setVisibility(JOURNAL_VISIBILITY visibility) { this.visibility = visibility; }
}

