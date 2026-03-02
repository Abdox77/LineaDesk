package com.linea_desk.rest_linea.Task;


import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.linea_desk.rest_linea.common.dto.ApiResponse;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.Project.Project;
import com.linea_desk.rest_linea.Project.ProjectRepository;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Log4j2
@RequestMapping("/api")
@RestController
public class TaskControllers {
    private final TaskServices taskServices;
    private static final Logger log = LogManager.getLogger(TaskControllers.class);


    public TaskControllers(TaskServices taskServices) {
        this.taskServices = taskServices;
    }

    @PostMapping("/task")
    public ResponseEntity<ApiResponse<?>> createTask(
        @AuthenticationPrincipal User currentUser,
        @RequestBody TaskRequestDto requestDto
    ) {
        ApiResponse<?> response;

        try {
            Optional<TaskResponseDto> task = taskServices.createNewTask(requestDto, currentUser);
            if (task.isEmpty()) {
                throw new Exception("invalid operation task dto is empty");
            }

            TaskResponseDto responseDto = task.get();
            response = new ApiResponse<>(

                    true,
                    "task created successfully",
                    responseDto
            ); 

        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Failed to create task: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
 

    @GetMapping("/task/{id}")
    public ResponseEntity<ApiResponse<?>> getTask(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            Optional<TaskResponseDto> task = taskServices.getTaskById(id, currentUser);
            if (task.isPresent()) {
                response = new ApiResponse<>(
                    true,
                    "Task retrieved successfully",
                    task.get()
                );
            } else {
                response = new ApiResponse<>(
                    false,
                    "Task not found"
                );
            }
        } catch (NumberFormatException e) {
            response = new ApiResponse<>(
                false,
                "Invalid task ID format"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response = new ApiResponse<>(
                    false,
                    "Failed to retrieve task: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/task/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTask(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            boolean deleted = taskServices.deleteTaskById(id, currentUser);
            if (deleted) {
                response = new ApiResponse<>(
                    true,
                    "Task deleted successfully"
                );
            } 
            else {
                response = new ApiResponse<>(
                    false,
                    "Failed to delete task"
                );
            }
        } catch (Exception e) {
            log.error("Error deleting task: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Failed to delete task: " + e.getMessage()
            );
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<ApiResponse<?>> updateTask(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long id,
        @RequestBody TaskRequestDto requestDto
    ) {
        ApiResponse<?> response;

        try {
            Optional<TaskResponseDto> task = taskServices.updateTask(id, requestDto, currentUser);

            if (task.isEmpty()) {
                response = new ApiResponse<>(
                    false,
                    "Task not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                true,
                "Task updated successfully",
                task.get()
            );
        } catch (Exception e) {
            log.error("Error updating task: {}", e.getMessage());
            response = new ApiResponse<>(
                false,
                "Internal Server Error",
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}