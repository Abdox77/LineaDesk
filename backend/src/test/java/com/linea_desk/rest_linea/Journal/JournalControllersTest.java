package com.linea_desk.rest_linea.Journal;

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
import com.linea_desk.rest_linea.Journal.Journal.JOURNAL_VISIBILITY;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.ApplicationConfiguration;
import com.linea_desk.rest_linea.config.GithubOAuthSuccessHandler;
import com.linea_desk.rest_linea.config.JwtAuthenticationFilter;
import com.linea_desk.rest_linea.config.SecurityConfiguration;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(controllers = JournalControllers.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApplicationConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GithubOAuthSuccessHandler.class)
    })
class JournalControllersTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private JournalServices journalServices;
    @MockitoBean private JwtService jwtService;

    private User testUser;
    private JournalRequestDto journalRequest;
    private JournalResponseDto journalResponse;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password123");
        journalRequest = new JournalRequestDto();
        journalRequest.setName("My Journal");
        journalRequest.setVisibility(JOURNAL_VISIBILITY.PRIVATE);
        journalResponse = new JournalResponseDto();
        journalResponse.setName("My Journal");
        journalResponse.setVisibility(JOURNAL_VISIBILITY.PRIVATE);
        journalResponse.setPages(Collections.emptyList());
    }


    @Test @WithMockUser
    void createNewJournal_Success() throws Exception {
        when(journalServices.createNewJournal(any(JournalRequestDto.class), any(User.class)))
                .thenReturn(journalResponse);
        mockMvc.perform(post("/api/journal").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Journal created successfully"))
                .andExpect(jsonPath("$.data.name").value("My Journal"));
    }

    @Test @WithMockUser
    void createNewJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.createNewJournal(any(JournalRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(post("/api/journal").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test @WithMockUser
    void getJournalById_Success() throws Exception {
        when(journalServices.getJournalById(eq(1L), any(User.class)))
                .thenReturn(journalResponse);
        mockMvc.perform(get("/api/journal/1").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("My Journal"));
    }

    @Test @WithMockUser
    void getJournalById_NotFound() throws Exception {
        when(journalServices.getJournalById(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Journal", 999L));
        mockMvc.perform(get("/api/journal/999").with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test @WithMockUser
    void getJournalById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.getJournalById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/journal/1").with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test @WithMockUser
    void getJournalsList_Success() throws Exception {
        JournalResponseDto j2 = new JournalResponseDto();
        j2.setName("Work Journal"); j2.setVisibility(JOURNAL_VISIBILITY.PUBLIC); j2.setPages(Collections.emptyList());
        Collection<JournalResponseDto> journals = List.of(journalResponse, j2);
        when(journalServices.getAllJournalsForUser(any(User.class))).thenReturn(journals);
        mockMvc.perform(get("/api/journals").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test @WithMockUser
    void getJournalsList_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.getAllJournalsForUser(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/journals").with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }


    @Test @WithMockUser
    void updateJournal_Success() throws Exception {
        JournalResponseDto updated = new JournalResponseDto();
        updated.setName("Updated Journal"); updated.setVisibility(JOURNAL_VISIBILITY.PUBLIC); updated.setPages(Collections.emptyList());
        when(journalServices.updateJournal(eq(1L), any(JournalRequestDto.class), any(User.class)))
                .thenReturn(updated);
        JournalRequestDto req = new JournalRequestDto();
        req.setName("Updated Journal"); req.setVisibility(JOURNAL_VISIBILITY.PUBLIC);
        mockMvc.perform(put("/api/journal/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Journal"));
    }

    @Test @WithMockUser
    void updateJournal_NotFound() throws Exception {
        when(journalServices.updateJournal(eq(999L), any(JournalRequestDto.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Journal", 999L));
        mockMvc.perform(put("/api/journal/999").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void updateJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.updateJournal(eq(1L), any(JournalRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(put("/api/journal/1").with(csrf()).with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isInternalServerError());
    }


    @Test @WithMockUser
    void deleteJournal_Success() throws Exception {
        doNothing().when(journalServices).deleteJournalById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/journal/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test @WithMockUser
    void deleteJournal_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Journal", 999L))
                .when(journalServices).deleteJournalById(eq(999L), any(User.class));
        mockMvc.perform(delete("/api/journal/999").with(csrf()).with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void deleteJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(journalServices).deleteJournalById(eq(1L), any(User.class));
        mockMvc.perform(delete("/api/journal/1").with(csrf()).with(user(testUser)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void createJournal_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/journal").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
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
