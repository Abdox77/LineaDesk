package com.linea_desk.rest_linea.Page;

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
public class PageControllers {

    private final PageServices pageServices;
    private static final Logger log = LogManager.getLogger(PageControllers.class);

    public PageControllers(PageServices pageServices) {
        this.pageServices = pageServices;
    }

    @PostMapping(path="/page")
    public ResponseEntity<ApiResponse<?>>
    createNewPage(
            @AuthenticationPrincipal User currentUser,
            @RequestBody PageRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<PageResponseDto> page = pageServices.createNewPage(request, currentUser);
            if (page.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Journal not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            PageResponseDto responseDto = page.get();
            response = new ApiResponse<>(
                    true,
                    "Page created successfully",
                    responseDto
            );
        } catch (Exception e) {
            log.error("Error creating page: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/page/{id}")
    public ResponseEntity<ApiResponse<?>>
    getPageById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            Optional<PageResponseDto> page = pageServices.getPageById(id, user);

            if (page.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Page not found"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Page retrieved successfully",
                    page.get()
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

    @GetMapping("/journal/{journalId}/pages")
    public ResponseEntity<ApiResponse<?>>
    getPagesByJournal(
            @AuthenticationPrincipal User user,
            @PathVariable Long journalId
    ) {
        ApiResponse<?> response;

        try {
            Optional<Collection<PageResponseDto>> pages = pageServices.getAllPagesForJournal(journalId, user);

            if (pages.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Journal not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Pages list retrieved successfully",
                    pages.get()
            );
        } catch (Exception e) {
            log.error("Error retrieving pages: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/page/{id}")
    public ResponseEntity<ApiResponse<?>>
    updatePage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody PageRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<PageResponseDto> page = pageServices.updatePage(id, request, user);

            if (page.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Page not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Page updated successfully",
                    page.get()
            );
        } catch (Exception e) {
            log.error("Error updating page: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/page/{id}")
    public ResponseEntity<ApiResponse<?>>
    deletePage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            boolean isDeleted = pageServices.deletePageById(id, user);
            if (!isDeleted) {
                response = new ApiResponse<>(
                        false,
                        "Page not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Page deleted successfully"
            );
        } catch (Exception e) {
            log.error("Error deleting page: {}", e.getMessage());
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
