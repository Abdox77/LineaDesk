package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.DuplicateResourceException;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;

@Service
public class ProjectServices {
    private final ProjectRepository projectRepository;

    public ProjectServices(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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

    public ProjectResponseDto getProjectById(Long id, User user) {
        Project project = projectRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        if (!project.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        return new ProjectResponseDto(project);
    }

    public void deleteProjectById(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        if (!project.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }

        projectRepository.delete(project);
    }

    public Collection<ProjectResponseDto> getAllProjectsForUser(User user) {
        Collection<Project> projects = projectRepository.findAllByUserWithTasks(user);
        return projects.stream()
                .map(ProjectResponseDto::new)
                .toList();
    }

    public ProjectResponseDto updateProject(Long id, ProjectRequestDto req, User user) {
        Project project = projectRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        if (!project.getUser().getUserId().equals(user.getUserId())) {
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
        return new ProjectResponseDto(project);
    }
}
