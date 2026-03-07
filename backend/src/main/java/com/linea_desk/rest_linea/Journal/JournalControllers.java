package com.linea_desk.rest_linea.Journal;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path="/api")
public class JournalControllers {

    private final JournalServices journalServices;

    public JournalControllers(JournalServices journalServices) {
        this.journalServices = journalServices;
    }

    @PostMapping(path="/journal")
    public ResponseEntity<ApiResponse<?>> createNewJournal(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody JournalRequestDto request
    ) {
        JournalResponseDto responseDto = journalServices.createNewJournal(request, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Journal created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/journal/{id}")
    public ResponseEntity<ApiResponse<?>> getJournalById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        JournalResponseDto journal = journalServices.getJournalById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Journal retrieved successfully", journal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/journals")
    public ResponseEntity<ApiResponse<?>> getJournalsList(
            @AuthenticationPrincipal User user
    ) {
        Collection<JournalResponseDto> journals = journalServices.getAllJournalsForUser(user);
        ApiResponse<?> response = new ApiResponse<>(true, "Journals list retrieved successfully", journals);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/journal/{id}")
    public ResponseEntity<ApiResponse<?>> updateJournal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody JournalRequestDto request
    ) {
        JournalResponseDto journal = journalServices.updateJournal(id, request, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Journal updated successfully", journal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/journal/{id}")
    public ResponseEntity<ApiResponse<?>> deleteJournal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        journalServices.deleteJournalById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Journal deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
