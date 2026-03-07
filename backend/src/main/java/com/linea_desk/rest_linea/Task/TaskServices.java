package com.linea_desk.rest_linea.Task;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.Project.Project;
import com.linea_desk.rest_linea.Project.ProjectRepository;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.exceptions.UnauthorizedAccessException;


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

    public TaskResponseDto createNewTask(TaskRequestDto taskRequestDto, User user) {
        Project project = projectRepository.findById(taskRequestDto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", taskRequestDto.getProjectId()));

        if (!project.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }

        Task task = new Task();

        task.setTaskName(taskRequestDto.getTaskName());
        task.setTaskDuration(taskRequestDto.getDuration());
        task.setTaskDescription(taskRequestDto.getDescription());
        task.setTaskState(taskRequestDto.getState());
        task.setTaskImportance(taskRequestDto.getImportance());
        task.setProject(project);

        if (taskRequestDto.getSortOrder() != null) {
            task.setSortOrder(taskRequestDto.getSortOrder());
        }
        if (taskRequestDto.getDueDate() != null) {
            task.setDueDate(taskRequestDto.getDueDate());
        }
        if (taskRequestDto.getParentTaskId() != null) {
            Task parent = taskRepository.findById(taskRequestDto.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Task", taskRequestDto.getParentTaskId()));
            task.setParentTask(parent);
        }

        taskRepository.save(task);
        return convertToDto(task);
    }

    public void deleteTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }

        taskRepository.delete(task);
    }

    public TaskResponseDto getTaskById(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }
        return convertToDto(task);
    }

    public TaskResponseDto updateTask(Long id, TaskRequestDto req, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException();
        }

        if (req.getTaskName() != null && !req.getTaskName().trim().isEmpty()) {
            task.setTaskName(req.getTaskName());
        }
        if (req.getDescription() != null) {
            task.setTaskDescription(req.getDescription());
        }
        if (req.getState() != null) {
            task.setTaskState(req.getState());
        }
        if (req.getImportance() != null) {
            task.setTaskImportance(req.getImportance());
        }
        task.setTaskDuration(req.getDuration());

        if (req.getSortOrder() != null) {
            task.setSortOrder(req.getSortOrder());
        }
        if (req.getDueDate() != null) {
            task.setDueDate(req.getDueDate());
        }
        if (req.getParentTaskId() != null) {
            Task parent = taskRepository.findById(req.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Task", req.getParentTaskId()));
            task.setParentTask(parent);
        }

        taskRepository.save(task);
        return convertToDto(task);
    }

    public void reorderTasks(List<TaskReorderDto> reorderList, User user) {
        for (TaskReorderDto item : reorderList) {
            Task task = taskRepository.findById(item.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task", item.getId()));

            if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
                throw new UnauthorizedAccessException();
            }

            task.setSortOrder(item.getSortOrder());
            taskRepository.save(task);
        }
    }

    public void bulkDeleteTasks(List<Long> taskIds, User user) {
        for (Long taskId : taskIds) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

            if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
                throw new UnauthorizedAccessException();
            }

            taskRepository.delete(task);
        }
    }

    public void bulkUpdateTaskState(List<Long> taskIds, Task.TASK_STATE state, User user) {
        for (Long taskId : taskIds) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

            if (!task.getProject().getUser().getUserId().equals(user.getUserId())) {
                throw new UnauthorizedAccessException();
            }

            task.setTaskState(state);
            taskRepository.save(task);
        }
    }
}
