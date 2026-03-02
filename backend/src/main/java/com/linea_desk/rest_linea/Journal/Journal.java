package com.linea_desk.rest_linea.Journal;

import java.util.Collection;

import com.linea_desk.rest_linea.Page.Page;
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
@Table(name="Journals")
public class Journal {
    public enum JOURNAL_VISIBILITY {
        PUBLIC,
        PRIVATE
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(length=255, nullable=false)
    @NotNull(message="Journal name cannot be Null")
    @NotBlank(message="Journal name cannot be Blank")
    @Size(min = 3, message = "Journal name must be at least 3 characters long")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition="varchar(255) default 'PRIVATE'")
    private JOURNAL_VISIBILITY visibility = JOURNAL_VISIBILITY.PRIVATE;

    @OneToMany(mappedBy="journal", cascade=CascadeType.ALL)
    private Collection<Page> pages;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Journal() { }

    public Journal(String name) {
        this.name = name;
    }

    public Journal(String name, JOURNAL_VISIBILITY visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public Long getJournalId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JOURNAL_VISIBILITY getVisibility() { return visibility; }
    public void setVisibility(JOURNAL_VISIBILITY visibility) { this.visibility = visibility; }

    public Collection<Page> getPages() { return pages; }
    public void addPage(Page page) {
        if (page != null) {
            this.pages.add(page);
        }
    }

    public User getUser() { return user; }
    public void setUser(User user) {
        if (user != null) {
            this.user = user;
        }
    }
}

