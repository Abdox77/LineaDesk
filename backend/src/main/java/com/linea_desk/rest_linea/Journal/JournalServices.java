package com.linea_desk.rest_linea.Journal;

import java.util.Collection;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.User.User;

@Log4j2
@Service
public class JournalServices {
    private final JournalRepository journalRepository;

    public JournalServices(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    public Optional<JournalResponseDto> createNewJournal(JournalRequestDto req, User user) {
        Journal journal = new Journal();

        journal.setName(req.getName());
        if (req.getVisibility() != null) {
            journal.setVisibility(req.getVisibility());
        }
        journal.setUser(user);
        journalRepository.save(journal);
        return Optional.of(new JournalResponseDto(journal));
    }

    public Optional<JournalResponseDto> getJournalById(Long id, User user) {
        Optional<Journal> journal = journalRepository.findByIdWithPages(id);

        if (journal.isEmpty()) {
            return Optional.empty();
        }

        if (!journal.get().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        return Optional.of(new JournalResponseDto(journal.get()));
    }

    public Optional<Collection<JournalResponseDto>> getAllJournalsForUser(User user) {
        Collection<Journal> journals = journalRepository.findAllByUserWithPages(user);
        Collection<JournalResponseDto> responseDtos = journals.stream()
                .map(JournalResponseDto::new)
                .toList();
        return Optional.of(responseDtos);
    }

    public Optional<JournalResponseDto> updateJournal(Long id, JournalRequestDto req, User user) {
        Optional<Journal> journalOpt = journalRepository.findByIdWithPages(id);

        if (journalOpt.isEmpty()) {
            return Optional.empty();
        }

        Journal journal = journalOpt.get();

        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            journal.setName(req.getName());
        }
        if (req.getVisibility() != null) {
            journal.setVisibility(req.getVisibility());
        }

        journalRepository.save(journal);
        return Optional.of(new JournalResponseDto(journal));
    }

    public boolean deleteJournalById(Long id, User user) {
        Optional<Journal> journalOpt = journalRepository.findById(id);

        if (journalOpt.isEmpty()) {
            return false;
        }

        Journal journal = journalOpt.get();

        if (!journal.getUser().getUserId().equals(user.getUserId())) {
            return false;
        }

        journalRepository.delete(journal);
        return true;
    }
}

