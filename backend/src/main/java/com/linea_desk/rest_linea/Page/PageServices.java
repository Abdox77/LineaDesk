package com.linea_desk.rest_linea.Page;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.Journal.Journal;
import com.linea_desk.rest_linea.Journal.JournalRepository;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;

@Service
public class PageServices {
    private final PageRepository pageRepository;
    private final JournalRepository journalRepository;

    public PageServices(PageRepository pageRepository, JournalRepository journalRepository) {
        this.pageRepository = pageRepository;
        this.journalRepository = journalRepository;
    }

    public PageResponseDto createNewPage(PageRequestDto req, User user) {
        Journal journal = journalRepository.findById(req.getJournalId())
                .orElseThrow(() -> new ResourceNotFoundException("Journal", req.getJournalId()));
        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        Page page = new Page();
        page.setTitle(req.getTitle());
        if (req.getContent() != null) {
            page.setContent(req.getContent());
        }
        page.setJournal(journal);
        pageRepository.save(page);
        return new PageResponseDto(page);
    }

    public PageResponseDto getPageById(Long id, User user) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page", id));
        if (!page.getJournal().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        return new PageResponseDto(page);
    }

    public Collection<PageResponseDto> getAllPagesForJournal(Long journalId, User user) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", journalId));
        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        Collection<Page> pages = pageRepository.findAllByJournal(journal);
        return pages.stream()
                .map(PageResponseDto::new)
                .toList();
    }

    public PageResponseDto updatePage(Long id, PageRequestDto req, User user) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page", id));
        if (!page.getJournal().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) {
            page.setTitle(req.getTitle());
        }
        if (req.getContent() != null) {
            page.setContent(req.getContent());
        }
        pageRepository.save(page);
        return new PageResponseDto(page);
    }

    public void deletePageById(Long id, User user) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page", id));
        if (!page.getJournal().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        pageRepository.delete(page);
    }
}
