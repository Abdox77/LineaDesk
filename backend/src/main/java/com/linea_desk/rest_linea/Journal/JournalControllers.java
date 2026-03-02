package com.linea_desk.rest_linea.Journal;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.PathVariable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.ApiResponse;

@Log4j2
@RestController
@RequestMapping(path="/api")
public class JournalControllers {

    private final JournalServices journalServices;
    private static final Logger log = LogManager.getLogger(JournalControllers.class);

    public JournalControllers(JournalServices journalServices) {
        this.journalServices = journalServices;
    }

    @PostMapping(path="/journal")
    public ResponseEntity<ApiResponse<?>>
    createNewJournal(
            @AuthenticationPrincipal User currentUser,
            @RequestBody JournalRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<JournalResponseDto> journal = journalServices.createNewJournal(request, currentUser);
            if (journal.isEmpty()) {
                throw new Exception("invalid operation journal dto is empty");
            }

            JournalResponseDto responseDto = journal.get();
            response = new ApiResponse<>(
                    true,
                    "Journal created successfully",
                    responseDto
            );
        } catch (Exception e) {
            log.error("Error creating journal: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/journal/{id}")
    public ResponseEntity<ApiResponse<?>>
    getJournalById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            Optional<JournalResponseDto> journal = journalServices.getJournalById(id, user);

            if (journal.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Journal not found"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Journal retrieved successfully",
                    journal.get()
            );
        } catch (Exception e) {
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/journals")
    public ResponseEntity<ApiResponse<?>>
    getJournalsList(
            @AuthenticationPrincipal User user
    ) {
        ApiResponse<?> response;

        try {
            Optional<Collection<JournalResponseDto>> journals = journalServices.getAllJournalsForUser(user);

            if (journals.isEmpty()) {
                log.error("No journals found for user with ID: {}", user.getUserId());
                response = new ApiResponse<>(
                        false,
                        "No journals found for the user"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Journals list retrieved successfully",
                    journals.get()
            );
        } catch (Exception e) {
            log.error("Error retrieving journals list: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/journal/{id}")
    public ResponseEntity<ApiResponse<?>>
    updateJournal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody JournalRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<JournalResponseDto> journal = journalServices.updateJournal(id, request, user);

            if (journal.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Journal not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Journal updated successfully",
                    journal.get()
            );
        } catch (Exception e) {
            log.error("Error updating journal: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/journal/{id}")
    public ResponseEntity<ApiResponse<?>>
    deleteJournal(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            boolean isDeleted = journalServices.deleteJournalById(id, user);
            if (!isDeleted) {
                response = new ApiResponse<>(
                        false,
                        "Journal not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Journal deleted successfully"
            );
        } catch (Exception e) {
            log.error("Error deleting journal: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}

