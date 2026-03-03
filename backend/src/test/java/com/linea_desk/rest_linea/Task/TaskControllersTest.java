package com.linea_desk.rest_linea.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linea_desk.rest_linea.Task.Task.TASK_IMPORTANCE;
import com.linea_desk.rest_linea.Task.Task.TASK_STATE;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.SecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskControllers.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class))
class TaskControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskServices taskServices;

    @MockitoBean
    private JwtService jwtService;

    private User testUser;
    private TaskRequestDto taskRequest;
    private TaskResponseDto taskResponse;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password123");

        taskRequest = new TaskRequestDto();
        taskRequest.setTaskName("Implement feature");
        taskRequest.setProjectId(1L);
        taskRequest.setDescription("Implement the login feature");
        taskRequest.setState(TASK_STATE.PENDING);
        taskRequest.setImportance(TASK_IMPORTANCE.IMPORTANT);
        taskRequest.setDuration(60);

        taskResponse = new TaskResponseDto();
        taskResponse.setId(1L);
        taskResponse.setTaskName("Implement feature");
        taskResponse.setProjectId(1L);
        taskResponse.setDescription("Implement the login feature");
        taskResponse.setState(TASK_STATE.PENDING);
        taskResponse.setImportance(TASK_IMPORTANCE.IMPORTANT);
    }

    // ========== POST /api/task ==========

    @Test
    @WithMockUser
    void createTask_Success() throws Exception {
        when(taskServices.createNewTask(any(TaskRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(taskResponse));

        mockMvc.perform(post("/api/task")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("task created successfully"))
                .andExpect(jsonPath("$.data.taskName").value("Implement feature"));
    }

    @Test
    @WithMockUser
    void createTask_ServiceReturnsEmpty_ReturnsInternalServerError() throws Exception {
        when(taskServices.createNewTask(any(TaskRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    void createTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(taskServices.createNewTask(any(TaskRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/task")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/task/{id} ==========

    @Test
    @WithMockUser
    void getTask_Success() throws Exception {
        when(taskServices.getTaskById(eq(1L), any(User.class)))
                .thenReturn(Optional.of(taskResponse));

        mockMvc.perform(get("/api/task/1")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task retrieved successfully"))
                .andExpect(jsonPath("$.data.taskName").value("Implement feature"));
    }

    @Test
    @WithMockUser
    void getTask_NotFound() throws Exception {
        when(taskServices.getTaskById(eq(999L), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/task/999")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    @WithMockUser
    void getTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(taskServices.getTaskById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/task/1")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== PUT /api/task/{id} ==========

    @Test
    @WithMockUser
    void updateTask_Success() throws Exception {
        TaskResponseDto updatedResponse = new TaskResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setTaskName("Updated task");
        updatedResponse.setProjectId(1L);
        updatedResponse.setState(TASK_STATE.IN_PROGRESS);
        updatedResponse.setImportance(TASK_IMPORTANCE.CRUCIAL);

        when(taskServices.updateTask(eq(1L), any(TaskRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(updatedResponse));

        TaskRequestDto updateRequest = new TaskRequestDto();
        updateRequest.setTaskName("Updated task");
        updateRequest.setState(TASK_STATE.IN_PROGRESS);
        updateRequest.setImportance(TASK_IMPORTANCE.CRUCIAL);

        mockMvc.perform(put("/api/task/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task updated successfully"))
                .andExpect(jsonPath("$.data.taskName").value("Updated task"))
                .andExpect(jsonPath("$.data.state").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser
    void updateTask_NotFound() throws Exception {
        when(taskServices.updateTask(eq(999L), any(TaskRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/task/999")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Task not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void updateTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(taskServices.updateTask(eq(1L), any(TaskRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/api/task/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== DELETE /api/task/{id} ==========

    @Test
    @WithMockUser
    void deleteTask_Success() throws Exception {
        when(taskServices.deleteTaskById(eq(1L), any(User.class)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/task/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));
    }

    @Test
    @WithMockUser
    void deleteTask_NotDeleted() throws Exception {
        when(taskServices.deleteTaskById(eq(999L), any(User.class)))
                .thenReturn(false);

        mockMvc.perform(delete("/api/task/999")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to delete task"));
    }

    @Test
    @WithMockUser
    void deleteTask_ServiceThrowsException() throws Exception {
        when(taskServices.deleteTaskById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(delete("/api/task/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== Auth test ==========

    @Test
    void createTask_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/task")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isUnauthorized());
    }
}
