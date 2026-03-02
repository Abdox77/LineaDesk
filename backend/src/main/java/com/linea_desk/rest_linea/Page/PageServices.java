package com.linea_desk.rest_linea.Page;

import java.util.Collection;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.Journal.Journal;
import com.linea_desk.rest_linea.Journal.JournalRepository;
import com.linea_desk.rest_linea.User.User;

@Log4j2
@Service
public class PageServices {
    private final PageRepository pageRepository;
    private final JournalRepository journalRepository;

    public PageServices(PageRepository pageRepository, JournalRepository journalRepository) {
        this.pageRepository = pageRepository;
        this.journalRepository = journalRepository;
    }

    public Optional<PageResponseDto> createNewPage(PageRequestDto req, User user) {
        Optional<Journal> journalOpt = journalRepository.findById(req.getJournalId());
        if (journalOpt.isEmpty()) {
            return Optional.empty();
        }

        if (!journalOpt.get().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        Page page = new Page();
        page.setTitle(req.getTitle());
        if (req.getContent() != null) {
            page.setContent(req.getContent());
        }
        page.setJournal(journalOpt.get());

        pageRepository.save(page);
        return Optional.of(new PageResponseDto(page));
    }

    public Optional<PageResponseDto> getPageById(Long id, User user) {
        Optional<Page> page = pageRepository.findById(id);

        if (page.isEmpty()) {
            return Optional.empty();
        }

        if (!page.get().getJournal().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        return Optional.of(new PageResponseDto(page.get()));
    }

    public Optional<Collection<PageResponseDto>> getAllPagesForJournal(Long journalId, User user) {
        Optional<Journal> journalOpt = journalRepository.findById(journalId);
        if (journalOpt.isEmpty()) {
            return Optional.empty();
        }

        if (!journalOpt.get().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        Collection<Page> pages = pageRepository.findAllByJournal(journalOpt.get());
        Collection<PageResponseDto> responseDtos = pages.stream()
                .map(PageResponseDto::new)
                .toList();
        return Optional.of(responseDtos);
    }

    public Optional<PageResponseDto> updatePage(Long id, PageRequestDto req, User user) {
        Optional<Page> pageOpt = pageRepository.findById(id);

        if (pageOpt.isEmpty()) {
            return Optional.empty();
        }

        Page page = pageOpt.get();

        if (!page.getJournal().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) {
            page.setTitle(req.getTitle());
        }
        if (req.getContent() != null) {
            page.setContent(req.getContent());
        }

        pageRepository.save(page);
        return Optional.of(new PageResponseDto(page));
    }

    public boolean deletePageById(Long id, User user) {
        Optional<Page> pageOpt = pageRepository.findById(id);

        if (pageOpt.isEmpty()) {
            return false;
        }

        Page page = pageOpt.get();

        if (!page.getJournal().getUser().getUserId().equals(user.getUserId())) {
            return false;
        }

        pageRepository.delete(page);
        return true;
    }
}
