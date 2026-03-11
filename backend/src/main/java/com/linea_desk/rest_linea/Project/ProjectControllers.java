package com.linea_desk.rest_linea.Project;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path="/api")
public class ProjectControllers {

    private final ProjectServices projectServices;

    public ProjectControllers(ProjectServices projectServices) {
        this.projectServices = projectServices;
    }

    @PostMapping(path="/project")
    public ResponseEntity<ApiResponse<?>> createNewProject(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ProjectRequestDto request
    ) {
        ProjectResponseDto responseDto = projectServices.createNewProject(request, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Project created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/project/{id}")
    public ResponseEntity<ApiResponse<?>> getProjectById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ProjectResponseDto project = projectServices.getProjectById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Project search was a success", project);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<?>> getProjectsList(
            @AuthenticationPrincipal User user
    ) {
        Collection<ProjectResponseDto> projects = projectServices.getAllProjectsForUser(user);
        ApiResponse<?> response = new ApiResponse<>(true, "Projects list retrieved successfully", projects);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<ApiResponse<?>> deleteProject(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        projectServices.deleteProjectById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Project deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<ApiResponse<?>> updateProject(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto request
    ) {
        ProjectResponseDto project = projectServices.updateProject(id, request, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Project updated successfully", project);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/project/{id}/invite")
    public ResponseEntity<ApiResponse<?>> generateInvite(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ProjectInvite invite = projectServices.generateInviteLink(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Invite generated", Map.of(
                "token", invite.getToken(),
                "expiresAt", invite.getExpiresAt().toString()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/projects/join")
    public ResponseEntity<ApiResponse<?>> joinByInvite(
            @AuthenticationPrincipal User user,
            @RequestParam String token
    ) {
        ProjectResponseDto project = projectServices.joinByInvite(token, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Joined project successfully", project);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{id}/members")
    public ResponseEntity<ApiResponse<?>> getMembers(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        List<ProjectMemberResponseDto> members = projectServices.getMembers(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Members retrieved", members);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/project/{id}/member/{userId}")
    public ResponseEntity<ApiResponse<?>> removeMember(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        projectServices.removeMember(id, userId, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Member removed");
        return ResponseEntity.ok(response);
    }
}
