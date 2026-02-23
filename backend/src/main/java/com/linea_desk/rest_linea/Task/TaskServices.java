package com.linea_desk.rest_linea.Task;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.Project.Project;
import com.linea_desk.rest_linea.Project.ProjectRepository;
import com.linea_desk.rest_linea.User.User;


@Service
public class TaskServices {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;    

    public TaskServices(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public TaskResponseDto convertToDto(Task task) {
        return new TaskResponseDto(task);
    }

    public Optional<TaskResponseDto> createNewTask(TaskRequestDto taskRequestDto, User user) {
        Optional<Project> projectOpt = projectRepository.findById(taskRequestDto.getProjectId());
        if (projectOpt.isEmpty()) {
            return Optional.empty();
        }

        if (!projectOpt.get().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }

        Task task = new Task();

        task.setTaskName(taskRequestDto.getTaskName());
        task.setTaskDuration(taskRequestDto.getDuration());
        task.setTaskDescription(taskRequestDto.getDescription());
        task.setTaskState(taskRequestDto.getState());
        task.setTaskImportance(taskRequestDto.getImportance());
        task.setProject(projectOpt.get());

        taskRepository.save(task);
        return Optional.of(convertToDto(task));
    }

    public boolean deleteTaskById(Long id, User user) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
            return false;
        }

        taskRepository.delete(taskOpt.get());
        return true;
    }

    public Optional<TaskResponseDto> getTaskById(Long id, User user) {
        Optional<Task> task = taskRepository.findById(id);

        if (task.isEmpty()) {
            return Optional.empty();
        }
        if (!task.get().getProject().getUser().getUserId().equals(user.getUserId())) {
            return Optional.empty();
        }
        return task.map(this::convertToDto);
    }
}
