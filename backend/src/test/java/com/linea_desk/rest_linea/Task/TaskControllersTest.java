package com.linea_desk.rest_linea.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linea_desk.rest_linea.Task.Task.TASK_IMPORTANCE;
import com.linea_desk.rest_linea.Task.Task.TASK_STATE;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.ApplicationConfiguration;
import com.linea_desk.rest_linea.config.GithubOAuthSuccessHandler;
import com.linea_desk.rest_linea.config.JwtAuthenticationFilter;
import com.linea_desk.rest_linea.config.SecurityConfiguration;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(controllers = TaskControllers.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApplicationConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GithubOAuthSuccessHandler.class)
    })
class TaskControllersTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private TaskServices taskServices;
    @MockitoBean private JwtService jwtService;

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


    @Test @WithMockUser
    void createTask_Success() throws Exception {
        when(taskServices.createNewTask(any(TaskRequestDto.class), any(User.class)))
                .thenReturn(taskResponse);
        mockMvc.perform(post("/api/task").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.data.taskName").value("Implement feature"));
    }

    @Test @WithMockUser
    void createTask_ProjectNotFound_ReturnsNotFound() throws Exception {
        when(taskServices.createNewTask(any(TaskRequestDto.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Project", 1L));
        mockMvc.perform(post("/api/task").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void createTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(taskServices.createNewTask(any(TaskRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/task").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isInternalServerError());
    }


    @Test @WithMockUser
    void getTask_Success() throws Exception {
        when(taskServices.getTaskById(eq(1L), any(User.class)))
                .thenReturn(taskResponse);
        mockMvc.perform(get("/api/task/1").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.taskName").value("Implement feature"));
    }

    @Test @WithMockUser
    void getTask_NotFound() throws Exception {
        when(taskServices.getTaskById(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Task", 999L));
        mockMvc.perform(get("/api/task/999").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void getTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(taskServices.getTaskById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/task/1").with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }


    @Test @WithMockUser
    void updateTask_Success() throws Exception {
        TaskResponseDto updated = new TaskResponseDto();
        updated.setId(1L); updated.setTaskName("Updated task"); updated.setProjectId(1L);
        updated.setState(TASK_STATE.IN_PROGRESS); updated.setImportance(TASK_IMPORTANCE.CRUCIAL);
        when(taskServices.updateTask(eq(1L), any(TaskRequestDto.class), any(User.class)))
                .thenReturn(updated);
        TaskRequestDto req = new TaskRequestDto();
        req.setTaskName("Updated task"); req.setState(TASK_STATE.IN_PROGRESS); req.setImportance(TASK_IMPORTANCE.CRUCIAL);
        mockMvc.perform(put("/api/task/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.taskName").value("Updated task"))
                .andExpect(jsonPath("$.data.state").value("IN_PROGRESS"));
    }

    @Test @WithMockUser
    void updateTask_NotFound() throws Exception {
        when(taskServices.updateTask(eq(999L), any(TaskRequestDto.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Task", 999L));
        mockMvc.perform(put("/api/task/999").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void updateTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(taskServices.updateTask(eq(1L), any(TaskRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(put("/api/task/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isInternalServerError());
    }


    @Test @WithMockUser
    void deleteTask_Success() throws Exception {
        doNothing().when(taskServices).deleteTaskById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/task/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test @WithMockUser
    void deleteTask_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Task", 999L))
                .when(taskServices).deleteTaskById(eq(999L), any(User.class));
        mockMvc.perform(delete("/api/task/999").with(csrf()).with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void deleteTask_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(taskServices).deleteTaskById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/task/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void createTask_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/task").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                    (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
            return http.build();
        }
    }
}
