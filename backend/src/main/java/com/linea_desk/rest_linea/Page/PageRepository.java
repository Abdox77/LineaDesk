package com.linea_desk.rest_linea.Page;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linea_desk.rest_linea.Journal.Journal;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Collection<Page> findAllByJournal(Journal journal);
    Optional<Page> findByTitle(String title);
}

