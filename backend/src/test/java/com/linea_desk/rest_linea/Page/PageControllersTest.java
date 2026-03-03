package com.linea_desk.rest_linea.Page;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PageControllers.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class))
class PageControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PageServices pageServices;

    @MockitoBean
    private JwtService jwtService;

    private User testUser;
    private PageRequestDto pageRequest;
    private PageResponseDto pageResponse;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password123");

        pageRequest = new PageRequestDto();
        pageRequest.setTitle("My Page");
        pageRequest.setContent("Some content here");
        pageRequest.setJournalId(1L);

        pageResponse = new PageResponseDto();
        pageResponse.setTitle("My Page");
        pageResponse.setContent("Some content here");
        pageResponse.setJournalId(1L);
    }

    // ========== POST /api/page ==========

    @Test
    @WithMockUser
    void createNewPage_Success() throws Exception {
        when(pageServices.createNewPage(any(PageRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(pageResponse));

        mockMvc.perform(post("/api/page")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Page created successfully"))
                .andExpect(jsonPath("$.data.title").value("My Page"))
                .andExpect(jsonPath("$.data.content").value("Some content here"));
    }

    @Test
    @WithMockUser
    void createNewPage_JournalNotFound_ReturnsNotFound() throws Exception {
        when(pageServices.createNewPage(any(PageRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/page")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Journal not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void createNewPage_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.createNewPage(any(PageRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/page")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/page/{id} ==========

    @Test
    @WithMockUser
    void getPageById_Success() throws Exception {
        when(pageServices.getPageById(eq(1L), any(User.class)))
                .thenReturn(Optional.of(pageResponse));

        mockMvc.perform(get("/api/page/1")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Page retrieved successfully"))
                .andExpect(jsonPath("$.data.title").value("My Page"));
    }

    @Test
    @WithMockUser
    void getPageById_NotFound() throws Exception {
        when(pageServices.getPageById(eq(999L), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/page/999")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Page not found"));
    }

    @Test
    @WithMockUser
    void getPageById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.getPageById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/page/1")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/journal/{journalId}/pages ==========

    @Test
    @WithMockUser
    void getPagesByJournal_Success() throws Exception {
        PageResponseDto page2 = new PageResponseDto();
        page2.setTitle("Second Page");
        page2.setContent("More content");
        page2.setJournalId(1L);

        Collection<PageResponseDto> pages = List.of(pageResponse, page2);
        when(pageServices.getAllPagesForJournal(eq(1L), any(User.class)))
                .thenReturn(Optional.of(pages));

        mockMvc.perform(get("/api/journal/1/pages")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pages list retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser
    void getPagesByJournal_JournalNotFound_ReturnsNotFound() throws Exception {
        when(pageServices.getAllPagesForJournal(eq(999L), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/journal/999/pages")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Journal not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void getPagesByJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.getAllPagesForJournal(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/journal/1/pages")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== PUT /api/page/{id} ==========

    @Test
    @WithMockUser
    void updatePage_Success() throws Exception {
        PageResponseDto updatedResponse = new PageResponseDto();
        updatedResponse.setTitle("Updated Page");
        updatedResponse.setContent("Updated content");
        updatedResponse.setJournalId(1L);

        when(pageServices.updatePage(eq(1L), any(PageRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(updatedResponse));

        PageRequestDto updateRequest = new PageRequestDto();
        updateRequest.setTitle("Updated Page");
        updateRequest.setContent("Updated content");

        mockMvc.perform(put("/api/page/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Page updated successfully"))
                .andExpect(jsonPath("$.data.title").value("Updated Page"));
    }

    @Test
    @WithMockUser
    void updatePage_NotFound() throws Exception {
        when(pageServices.updatePage(eq(999L), any(PageRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/page/999")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Page not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void updatePage_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.updatePage(eq(1L), any(PageRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/api/page/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== DELETE /api/page/{id} ==========

    @Test
    @WithMockUser
    void deletePage_Success() throws Exception {
        when(pageServices.deletePageById(eq(1L), any(User.class)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/page/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Page deleted successfully"));
    }

    @Test
    @WithMockUser
    void deletePage_NotFound() throws Exception {
        when(pageServices.deletePageById(eq(999L), any(User.class)))
                .thenReturn(false);

        mockMvc.perform(delete("/api/page/999")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Page not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void deletePage_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.deletePageById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(delete("/api/page/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== Auth test ==========

    @Test
    void createPage_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/page")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isUnauthorized());
    }
}

