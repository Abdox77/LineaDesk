package com.linea_desk.rest_linea.Task;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.linea_desk.rest_linea.common.dto.ApiResponse;
import com.linea_desk.rest_linea.User.User;

import jakarta.validation.Valid;

@RequestMapping("/api")
@RestController
public class TaskControllers {
    private final TaskServices taskServices;

    public TaskControllers(TaskServices taskServices) {
        this.taskServices = taskServices;
    }

    @PostMapping("/task")
    public ResponseEntity<ApiResponse<?>> createTask(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody TaskRequestDto requestDto
    ) {
        TaskResponseDto responseDto = taskServices.createNewTask(requestDto, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Task created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<ApiResponse<?>> getTask(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long id
    ) {
        TaskResponseDto task = taskServices.getTaskById(id, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Task retrieved successfully", task);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTask(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long id
    ) {
        taskServices.deleteTaskById(id, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Task deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<ApiResponse<?>> updateTask(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long id,
        @Valid @RequestBody TaskRequestDto requestDto
    ) {
        TaskResponseDto task = taskServices.updateTask(id, requestDto, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Task updated successfully", task);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/reorder")
    public ResponseEntity<ApiResponse<?>> reorderTasks(
        @AuthenticationPrincipal User currentUser,
        @RequestBody List<TaskReorderDto> reorderList
    ) {
        taskServices.reorderTasks(reorderList, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Tasks reordered successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/bulk")
    public ResponseEntity<ApiResponse<?>> bulkDeleteTasks(
        @AuthenticationPrincipal User currentUser,
        @RequestBody List<Long> taskIds
    ) {
        taskServices.bulkDeleteTasks(taskIds, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Tasks deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PutMapping("/tasks/bulk-state")
    public ResponseEntity<ApiResponse<?>> bulkUpdateTaskState(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody BulkTaskStateDto dto
    ) {
        taskServices.bulkUpdateTaskState(dto.getTaskIds(), dto.getState(), currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Tasks state updated successfully");
        return ResponseEntity.ok(response);
    }
}