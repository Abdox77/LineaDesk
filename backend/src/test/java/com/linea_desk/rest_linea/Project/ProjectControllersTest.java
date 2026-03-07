package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.DuplicateResourceException;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.ApplicationConfiguration;
import com.linea_desk.rest_linea.config.GithubOAuthSuccessHandler;
import com.linea_desk.rest_linea.config.JwtAuthenticationFilter;
import com.linea_desk.rest_linea.config.SecurityConfiguration;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(
    controllers = ProjectControllers.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApplicationConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GithubOAuthSuccessHandler.class)
    }
)
class ProjectControllersTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private ProjectServices projectServices;
    @MockitoBean private JwtService jwtService;

    private User testUser;
    private ProjectRequestDto projectRequest;
    private ProjectResponseDto projectResponse;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password123");
        projectRequest = new ProjectRequestDto();
        projectRequest.setProjectName("My Project");
        projectRequest.setDescription("A test project");
        projectRequest.setGithubLink("https://github.com/test/project");
        projectResponse = new ProjectResponseDto();
        projectResponse.setProjectName("My Project");
        projectResponse.setDescription("A test project");
        projectResponse.setGithubLink("https://github.com/test/project");
        projectResponse.setSessions(0);
        projectResponse.setTasks(Collections.emptyList());
    }

    // ========== POST /api/project ==========

    @Test @WithMockUser
    void createNewProject_Success() throws Exception {
        when(projectServices.createNewProject(any(ProjectRequestDto.class), any(User.class)))
                .thenReturn(projectResponse);
        mockMvc.perform(post("/api/project").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project created successfully"))
                .andExpect(jsonPath("$.data.projectName").value("My Project"));
    }

    @Test @WithMockUser
    void createNewProject_DuplicateName_ReturnsConflict() throws Exception {
        when(projectServices.createNewProject(any(ProjectRequestDto.class), any(User.class)))
                .thenThrow(new DuplicateResourceException("Project", "name", "My Project"));
        mockMvc.perform(post("/api/project").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test @WithMockUser
    void createNewProject_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.createNewProject(any(ProjectRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/project").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isInternalServerError());
    }

    // ========== GET /api/project/{id} ==========

    @Test @WithMockUser
    void getProjectById_Success() throws Exception {
        when(projectServices.getProjectById(eq(1L), any(User.class)))
                .thenReturn(projectResponse);
        mockMvc.perform(get("/api/project/1").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectName").value("My Project"));
    }

    @Test @WithMockUser
    void getProjectById_NotFound() throws Exception {
        when(projectServices.getProjectById(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Project", 999L));
        mockMvc.perform(get("/api/project/999").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void getProjectById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.getProjectById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/project/1").with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }

    // ========== GET /api/projects ==========

    @Test @WithMockUser
    void getProjectsList_Success() throws Exception {
        ProjectResponseDto p2 = new ProjectResponseDto();
        p2.setProjectName("Another Project"); p2.setDescription("Second"); p2.setSessions(3); p2.setTasks(Collections.emptyList());
        Collection<ProjectResponseDto> projects = List.of(projectResponse, p2);
        when(projectServices.getAllProjectsForUser(any(User.class))).thenReturn(projects);
        mockMvc.perform(get("/api/projects").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test @WithMockUser
    void getProjectsList_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.getAllProjectsForUser(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/projects").with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }

    // ========== PUT /api/project/{id} ==========

    @Test @WithMockUser
    void updateProject_Success() throws Exception {
        ProjectResponseDto updated = new ProjectResponseDto();
        updated.setProjectName("Updated Project"); updated.setDescription("Updated desc"); updated.setSessions(5); updated.setTasks(Collections.emptyList());
        when(projectServices.updateProject(eq(1L), any(ProjectRequestDto.class), any(User.class)))
                .thenReturn(updated);
        ProjectRequestDto req = new ProjectRequestDto();
        req.setProjectName("Updated Project"); req.setDescription("Updated desc"); req.setSessions(5);
        mockMvc.perform(put("/api/project/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectName").value("Updated Project"));
    }

    @Test @WithMockUser
    void updateProject_NotFound() throws Exception {
        when(projectServices.updateProject(eq(999L), any(ProjectRequestDto.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Project", 999L));
        mockMvc.perform(put("/api/project/999").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void updateProject_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.updateProject(eq(1L), any(ProjectRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(put("/api/project/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isInternalServerError());
    }

    // ========== DELETE /api/project/{id} ==========

    @Test @WithMockUser
    void deleteProject_Success() throws Exception {
        doNothing().when(projectServices).deleteProjectById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/project/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test @WithMockUser
    void deleteProject_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Project", 999L))
                .when(projectServices).deleteProjectById(eq(999L), any(User.class));
        mockMvc.perform(delete("/api/project/999").with(csrf()).with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void deleteProject_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(projectServices).deleteProjectById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/project/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }

    // ========== Auth test ==========

    @Test
    void createNewProject_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/project").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
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

