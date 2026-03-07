package com.linea_desk.rest_linea.Page;

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
public class PageControllers {

    private final PageServices pageServices;

    public PageControllers(PageServices pageServices) {
        this.pageServices = pageServices;
    }

    @PostMapping(path="/page")
    public ResponseEntity<ApiResponse<?>> createNewPage(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody PageRequestDto request
    ) {
        PageResponseDto responseDto = pageServices.createNewPage(request, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Page created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/page/{id}")
    public ResponseEntity<ApiResponse<?>> getPageById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        PageResponseDto page = pageServices.getPageById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Page retrieved successfully", page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/journal/{journalId}/pages")
    public ResponseEntity<ApiResponse<?>> getPagesByJournal(
            @AuthenticationPrincipal User user,
            @PathVariable Long journalId
    ) {
        Collection<PageResponseDto> pages = pageServices.getAllPagesForJournal(journalId, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Pages list retrieved successfully", pages);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/page/{id}")
    public ResponseEntity<ApiResponse<?>> updatePage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody PageRequestDto request
    ) {
        PageResponseDto page = pageServices.updatePage(id, request, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Page updated successfully", page);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/page/{id}")
    public ResponseEntity<ApiResponse<?>> deletePage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        pageServices.deletePageById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Page deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
