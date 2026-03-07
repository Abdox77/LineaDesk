package com.linea_desk.rest_linea.Journal;

import com.linea_desk.rest_linea.Journal.Journal.JOURNAL_VISIBILITY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JournalRequestDto {
    @NotNull(message = "Journal name cannot be null")
    @NotBlank(message = "Journal name cannot be blank")
    @Size(min = 3, message = "Journal name must be at least 3 characters long")
    private String name;

    private JOURNAL_VISIBILITY visibility;

    public JournalRequestDto() { }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JOURNAL_VISIBILITY getVisibility() { return visibility; }
    public void setVisibility(JOURNAL_VISIBILITY visibility) { this.visibility = visibility; }
}
