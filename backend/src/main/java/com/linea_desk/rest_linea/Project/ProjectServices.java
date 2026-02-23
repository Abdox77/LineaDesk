package com.linea_desk.rest_linea.Project;

import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;
import java.util.Collection;

import com.linea_desk.rest_linea.User.User;

@Log4j2
@Service
public class ProjectServices {
    private final ProjectRepository projectRepository;
    private static final Logger log = LogManager.getLogger(ProjectServices.class);

    public ProjectServices(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<ProjectResponseDto> createNewProject(ProjectRequestDto req, User user) {
        Project project = new Project();

        /*
            here i should check for whether the project exists or not
            and i should throw customized exception

        */

        project.setProjectName(req.getProjectName());
        project.setDescription(req.getDescription());
        if(req.getGithubLink() != null) {
            project.setGithubLink(req.getGithubLink());
        }
        project.setUser(user);
        projectRepository.save(project);
        return Optional.of(new ProjectResponseDto(project));
    }

    public Optional<ProjectResponseDto> getProjectById(Long id, User user) {
        Optional<Project> project = projectRepository.findByIdWithTasks(id);

        if (project.isEmpty()) {
            return Optional.empty();
        }

        if (!project.get().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }
        return Optional.of(new ProjectResponseDto(project.get()));
    }

    public boolean deleteProjectById(Long id, User user) {
        Optional<Project> projectOpt = projectRepository.findById(id);

        if (projectOpt.isEmpty()) {
            return false;
        }

        Project project = projectOpt.get();

        if (!project.getUser().getUserId().equals(user.getUserId())) {
            return false;
        }

        projectRepository.delete(project);
        return true;
    }

    public Optional<Collection<ProjectResponseDto>> getAllProjectsForUser(User user) {
        Collection<Project> projects = projectRepository.findAllByUserWithTasks(user);
        Collection<ProjectResponseDto> responseDtos = projects.stream()
                .map(ProjectResponseDto::new)
                .toList();
        return Optional.of(responseDtos);
    }
}
