package com.linea_desk.rest_linea.Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linea_desk.rest_linea.Project.Project.PROJECT_STATE;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = ProjectControllers.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class)
)
class ProjectControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectServices projectServices;

    @MockitoBean
    private JwtService jwtService;

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

    @Test
    @WithMockUser
    void createNewProject_Success() throws Exception {
        when(projectServices.createNewProject(any(ProjectRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(projectResponse));

        mockMvc.perform(post("/api/project")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project created successfuly"))
                .andExpect(jsonPath("$.data.projectName").value("My Project"))
                .andExpect(jsonPath("$.data.description").value("A test project"));
    }

    @Test
    @WithMockUser
    void createNewProject_ServiceReturnsEmpty_ReturnsInternalServerError() throws Exception {
        when(projectServices.createNewProject(any(ProjectRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/project")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }

    @Test
    @WithMockUser
    void createNewProject_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.createNewProject(any(ProjectRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/project")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/project/{id} ==========

    @Test
    @WithMockUser
    void getProjectById_Success() throws Exception {
        when(projectServices.getProjectById(eq(1L), any(User.class)))
                .thenReturn(Optional.of(projectResponse));

        mockMvc.perform(get("/api/project/1")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project search was a success"))
                .andExpect(jsonPath("$.data.projectName").value("My Project"));
    }

    @Test
    @WithMockUser
    void getProjectById_NotFound() throws Exception {
        when(projectServices.getProjectById(eq(999L), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/project/999")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found"));
    }

    @Test
    @WithMockUser
    void getProjectById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.getProjectById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/project/1")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/projects ==========

    @Test
    @WithMockUser
    void getProjectsList_Success() throws Exception {
        ProjectResponseDto project2 = new ProjectResponseDto();
        project2.setProjectName("Another Project");
        project2.setDescription("Second project");
        project2.setSessions(3);
        project2.setTasks(Collections.emptyList());

        Collection<ProjectResponseDto> projects = List.of(projectResponse, project2);
        when(projectServices.getAllProjectsForUser(any(User.class)))
                .thenReturn(Optional.of(projects));

        mockMvc.perform(get("/api/projects")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Projects list retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser
    void getProjectsList_Empty_ReturnsNotFound() throws Exception {
        when(projectServices.getAllProjectsForUser(any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No projects found for the user"));
    }

    @Test
    @WithMockUser
    void getProjectsList_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.getAllProjectsForUser(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/projects")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== PUT /api/project/{id} ==========

    @Test
    @WithMockUser
    void updateProject_Success() throws Exception {
        ProjectResponseDto updatedResponse = new ProjectResponseDto();
        updatedResponse.setProjectName("Updated Project");
        updatedResponse.setDescription("Updated description");
        updatedResponse.setSessions(5);
        updatedResponse.setTasks(Collections.emptyList());

        when(projectServices.updateProject(eq(1L), any(ProjectRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(updatedResponse));

        ProjectRequestDto updateRequest = new ProjectRequestDto();
        updateRequest.setProjectName("Updated Project");
        updateRequest.setDescription("Updated description");
        updateRequest.setSessions(5);

        mockMvc.perform(put("/api/project/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project updated successfully"))
                .andExpect(jsonPath("$.data.projectName").value("Updated Project"));
    }

    @Test
    @WithMockUser
    void updateProject_NotFound() throws Exception {
        when(projectServices.updateProject(eq(999L), any(ProjectRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/project/999")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void updateProject_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.updateProject(eq(1L), any(ProjectRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/api/project/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== DELETE /api/project/{id} ==========

    @Test
    @WithMockUser
    void deleteProject_Success() throws Exception {
        when(projectServices.deleteProjectById(eq(1L), any(User.class)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/project/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));
    }

    @Test
    @WithMockUser
    void deleteProject_NotFound() throws Exception {
        when(projectServices.deleteProjectById(eq(999L), any(User.class)))
                .thenReturn(false);

        mockMvc.perform(delete("/api/project/999")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Project not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void deleteProject_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(projectServices.deleteProjectById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(delete("/api/project/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== Auth test ==========

    @Test
    void createNewProject_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/project")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isUnauthorized());
    }
}

