package com.linea_desk.rest_linea.Journal;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;

@Service
public class JournalServices {
    private final JournalRepository journalRepository;

    public JournalServices(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    public JournalResponseDto createNewJournal(JournalRequestDto req, User user) {
        Journal journal = new Journal();
        journal.setName(req.getName());
        if (req.getVisibility() != null) {
            journal.setVisibility(req.getVisibility());
        }
        journal.setUser(user);
        journalRepository.save(journal);
        return new JournalResponseDto(journal);
    }

    public JournalResponseDto getJournalById(Long id, User user) {
        Journal journal = journalRepository.findByIdWithPages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", id));
        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        return new JournalResponseDto(journal);
    }

    public Collection<JournalResponseDto> getAllJournalsForUser(User user) {
        Collection<Journal> journals = journalRepository.findAllByUserWithPages(user);
        return journals.stream()
                .map(JournalResponseDto::new)
                .toList();
    }

    public JournalResponseDto updateJournal(Long id, JournalRequestDto req, User user) {
        Journal journal = journalRepository.findByIdWithPages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", id));
        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            journal.setName(req.getName());
        }
        if (req.getVisibility() != null) {
            journal.setVisibility(req.getVisibility());
        }
        journalRepository.save(journal);
        return new JournalResponseDto(journal);
    }

    public void deleteJournalById(Long id, User user) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", id));
        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        journalRepository.delete(journal);
    }
}
