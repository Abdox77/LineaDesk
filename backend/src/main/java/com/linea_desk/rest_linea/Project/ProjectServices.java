package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.DuplicateResourceException;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;

@Service
public class ProjectServices {
    private final ProjectRepository projectRepository;
    private final ProjectInviteRepository projectInviteRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectServices(ProjectRepository projectRepository,
                           ProjectInviteRepository projectInviteRepository,
                           ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectInviteRepository = projectInviteRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    private boolean hasAccess(Project project, User user) {
        return project.getUser().getUserId().equals(user.getUserId())
                || projectMemberRepository.existsByProjectIdAndUserId(project.getProjectId(), user.getUserId());
    }

    public ProjectResponseDto createNewProject(ProjectRequestDto req, User user) {
        Optional<Project> existing = projectRepository.findByProjectName(req.getProjectName());
        if (existing.isPresent()) {
            throw new DuplicateResourceException("Project", "name", req.getProjectName());
        }
        Project project = new Project();
        project.setProjectName(req.getProjectName());
        project.setDescription(req.getDescription());
        if (req.getGithubLink() != null) {
            project.setGithubLink(req.getGithubLink());
        }
        project.setUser(user);
        projectRepository.save(project);
        return new ProjectResponseDto(project);
    }

    private List<ProjectMemberResponseDto> buildParticipants(Project project) {
        ProjectMemberResponseDto ownerDto = new ProjectMemberResponseDto(
                project.getUser().getUserId(),
                project.getUser().getDisplayName(),
                project.getUser().getEmail(),
                "OWNER"
        );
        List<ProjectMemberResponseDto> collaborators = projectMemberRepository.findByProjectId(project.getProjectId())
                .stream().map(ProjectMemberResponseDto::new).toList();
        List<ProjectMemberResponseDto> result = new java.util.ArrayList<>();
        result.add(ownerDto);
        result.addAll(collaborators);
        return result;
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long id, User user) {
        Project project = projectRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        if (!hasAccess(project, user)) {
            throw new UnauthorizedAccessException();
        }
        return new ProjectResponseDto(project, buildParticipants(project));
    }

    public void deleteProjectById(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        if (!project.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public Collection<ProjectResponseDto> getAllProjectsForUser(User user) {
        Collection<Project> owned = projectRepository.findAllByUserWithTasks(user);
        List<ProjectResponseDto> result = owned.stream()
                .map(p -> new ProjectResponseDto(p, buildParticipants(p)))
                .collect(java.util.stream.Collectors.toList());

        List<ProjectMember> memberships = projectMemberRepository.findByUserId(user.getUserId());
        for (ProjectMember m : memberships) {
            Project p = projectRepository.findByIdWithTasks(m.getProject().getProjectId()).orElse(null);
            if (p != null) {
                result.add(new ProjectResponseDto(p, buildParticipants(p)));
            }
        }
        return result;
    }

    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto req, User user) {
        Project project = projectRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        if (!hasAccess(project, user)) {
            throw new UnauthorizedAccessException();
        }
        if (req.getProjectName() != null && !req.getProjectName().trim().isEmpty()) {
            project.setProjectName(req.getProjectName());
        }
        if (req.getDescription() != null) {
            project.setDescription(req.getDescription());
        }
        if (req.getGithubLink() != null) {
            project.setGithubLink(req.getGithubLink());
        }
        if (req.getSessions() != null) {
            project.setSessions(req.getSessions());
        }
        if (req.getState() != null) {
            project.setState(req.getState());
        }
        projectRepository.save(project);
        return new ProjectResponseDto(project, buildParticipants(project));
    }

    public ProjectInvite generateInviteLink(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        if (!project.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        Optional<ProjectInvite> existing = projectInviteRepository
                .findByProjectIdAndStatus(projectId, ProjectInvite.INVITE_STATUS.PENDING);
        if (existing.isPresent() && !existing.get().isExpired()) {
            return existing.get();
        }
        existing.ifPresent(inv -> {
            inv.setStatus(ProjectInvite.INVITE_STATUS.EXPIRED);
            projectInviteRepository.save(inv);
        });
        ProjectInvite invite = new ProjectInvite(project);
        return projectInviteRepository.save(invite);
    }

    @Transactional
    public ProjectResponseDto joinByInvite(String token, User user) {
        ProjectInvite invite = projectInviteRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invite", 0L));
        if (invite.isExpired() || invite.getStatus() == ProjectInvite.INVITE_STATUS.EXPIRED) {
            throw new UnauthorizedAccessException();
        }
        Project project = projectRepository.findByIdWithTasks(invite.getProject().getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", invite.getProject().getProjectId()));
        if (project.getUser().getUserId().equals(user.getUserId())) {
            throw new DuplicateResourceException("Member", "user", user.getEmail());
        }
        if (!projectMemberRepository.existsByProjectIdAndUserId(project.getProjectId(), user.getUserId())) {
            ProjectMember member = new ProjectMember(project, user, ProjectMember.MEMBER_ROLE.COLLABORATOR);
            projectMemberRepository.save(member);
        }
        return new ProjectResponseDto(project, buildParticipants(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponseDto> getMembers(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        if (!hasAccess(project, user)) {
            throw new UnauthorizedAccessException();
        }
        return buildParticipants(project);
    }

    @Transactional
    public void removeMember(Long projectId, Long userId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        if (!project.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }
}
