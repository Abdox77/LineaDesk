package com.linea_desk.rest_linea.Page;

import java.util.Collection;
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
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.ApplicationConfiguration;
import com.linea_desk.rest_linea.config.GithubOAuthSuccessHandler;
import com.linea_desk.rest_linea.config.JwtAuthenticationFilter;
import com.linea_desk.rest_linea.config.SecurityConfiguration;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(controllers = PageControllers.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApplicationConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GithubOAuthSuccessHandler.class)
    })
class PageControllersTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private PageServices pageServices;
    @MockitoBean private JwtService jwtService;

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

    @Test @WithMockUser
    void createNewPage_Success() throws Exception {
        when(pageServices.createNewPage(any(PageRequestDto.class), any(User.class)))
                .thenReturn(pageResponse);
        mockMvc.perform(post("/api/page").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Page created successfully"))
                .andExpect(jsonPath("$.data.title").value("My Page"));
    }

    @Test @WithMockUser
    void createNewPage_JournalNotFound_ReturnsNotFound() throws Exception {
        when(pageServices.createNewPage(any(PageRequestDto.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Journal", 999L));
        mockMvc.perform(post("/api/page").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test @WithMockUser
    void createNewPage_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.createNewPage(any(PageRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/page").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test @WithMockUser
    void getPageById_Success() throws Exception {
        when(pageServices.getPageById(eq(1L), any(User.class)))
                .thenReturn(pageResponse);
        mockMvc.perform(get("/api/page/1").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("My Page"));
    }

    @Test @WithMockUser
    void getPageById_NotFound() throws Exception {
        when(pageServices.getPageById(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Page", 999L));
        mockMvc.perform(get("/api/page/999").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void getPageById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.getPageById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/page/1").with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test @WithMockUser
    void getPagesByJournal_Success() throws Exception {
        PageResponseDto p2 = new PageResponseDto();
        p2.setTitle("Second Page"); p2.setContent("More content"); p2.setJournalId(1L);
        Collection<PageResponseDto> pages = List.of(pageResponse, p2);
        when(pageServices.getAllPagesForJournal(eq(1L), any(User.class))).thenReturn(pages);
        mockMvc.perform(get("/api/journal/1/pages").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test @WithMockUser
    void getPagesByJournal_JournalNotFound_ReturnsNotFound() throws Exception {
        when(pageServices.getAllPagesForJournal(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Journal", 999L));
        mockMvc.perform(get("/api/journal/999/pages").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void getPagesByJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.getAllPagesForJournal(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/journal/1/pages").with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test @WithMockUser
    void updatePage_Success() throws Exception {
        PageResponseDto updated = new PageResponseDto();
        updated.setTitle("Updated Page"); updated.setContent("Updated content"); updated.setJournalId(1L);
        when(pageServices.updatePage(eq(1L), any(PageRequestDto.class), any(User.class)))
                .thenReturn(updated);
        PageRequestDto req = new PageRequestDto();
        req.setTitle("Updated Page"); req.setContent("Updated content"); req.setJournalId(1L);
        mockMvc.perform(put("/api/page/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Page"));
    }

    @Test @WithMockUser
    void updatePage_NotFound() throws Exception {
        when(pageServices.updatePage(eq(999L), any(PageRequestDto.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Page", 999L));
        mockMvc.perform(put("/api/page/999").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void updatePage_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(pageServices.updatePage(eq(1L), any(PageRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(put("/api/page/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test @WithMockUser
    void deletePage_Success() throws Exception {
        doNothing().when(pageServices).deletePageById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/page/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test @WithMockUser
    void deletePage_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Page", 999L))
                .when(pageServices).deletePageById(eq(999L), any(User.class));
        mockMvc.perform(delete("/api/page/999").with(csrf()).with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void deletePage_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(pageServices).deletePageById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/page/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPage_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/page").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
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

