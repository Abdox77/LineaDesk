package com.linea_desk.rest_linea.Journal;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.linea_desk.rest_linea.User.User;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    Optional<Journal> findByName(String name);

    @Query("SELECT j FROM Journal j LEFT JOIN FETCH j.pages WHERE j.user = :user")
    Collection<Journal> findAllByUserWithPages(@Param("user") User user);

    @Query("SELECT j FROM Journal j LEFT JOIN FETCH j.pages WHERE j.id = :id")
    Optional<Journal> findByIdWithPages(@Param("id") Long id);

    Collection<Journal> findAllByUser(User user);
}
