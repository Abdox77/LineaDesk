package com.linea_desk.rest_linea.Journal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linea_desk.rest_linea.Journal.Journal.JOURNAL_VISIBILITY;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = JournalControllers.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class))
class JournalControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JournalServices journalServices;

    @MockitoBean
    private JwtService jwtService;

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

    // ========== POST /api/journal ==========

    @Test
    @WithMockUser
    void createNewJournal_Success() throws Exception {
        when(journalServices.createNewJournal(any(JournalRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(journalResponse));

        mockMvc.perform(post("/api/journal")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Journal created successfully"))
                .andExpect(jsonPath("$.data.name").value("My Journal"))
                .andExpect(jsonPath("$.data.visibility").value("PRIVATE"));
    }

    @Test
    @WithMockUser
    void createNewJournal_ServiceReturnsEmpty_ReturnsInternalServerError() throws Exception {
        when(journalServices.createNewJournal(any(JournalRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/journal")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }

    @Test
    @WithMockUser
    void createNewJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.createNewJournal(any(JournalRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/journal")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/journal/{id} ==========

    @Test
    @WithMockUser
    void getJournalById_Success() throws Exception {
        when(journalServices.getJournalById(eq(1L), any(User.class)))
                .thenReturn(Optional.of(journalResponse));

        mockMvc.perform(get("/api/journal/1")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Journal retrieved successfully"))
                .andExpect(jsonPath("$.data.name").value("My Journal"));
    }

    @Test
    @WithMockUser
    void getJournalById_NotFound() throws Exception {
        when(journalServices.getJournalById(eq(999L), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/journal/999")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Journal not found"));
    }

    @Test
    @WithMockUser
    void getJournalById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.getJournalById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/journal/1")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/journals ==========

    @Test
    @WithMockUser
    void getJournalsList_Success() throws Exception {
        JournalResponseDto journal2 = new JournalResponseDto();
        journal2.setName("Work Journal");
        journal2.setVisibility(JOURNAL_VISIBILITY.PUBLIC);
        journal2.setPages(Collections.emptyList());

        Collection<JournalResponseDto> journals = List.of(journalResponse, journal2);
        when(journalServices.getAllJournalsForUser(any(User.class)))
                .thenReturn(Optional.of(journals));

        mockMvc.perform(get("/api/journals")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Journals list retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser
    void getJournalsList_Empty_ReturnsNotFound() throws Exception {
        when(journalServices.getAllJournalsForUser(any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/journals")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No journals found for the user"));
    }

    @Test
    @WithMockUser
    void getJournalsList_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.getAllJournalsForUser(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/journals")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== PUT /api/journal/{id} ==========

    @Test
    @WithMockUser
    void updateJournal_Success() throws Exception {
        JournalResponseDto updatedResponse = new JournalResponseDto();
        updatedResponse.setName("Updated Journal");
        updatedResponse.setVisibility(JOURNAL_VISIBILITY.PUBLIC);
        updatedResponse.setPages(Collections.emptyList());

        when(journalServices.updateJournal(eq(1L), any(JournalRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(updatedResponse));

        JournalRequestDto updateRequest = new JournalRequestDto();
        updateRequest.setName("Updated Journal");
        updateRequest.setVisibility(JOURNAL_VISIBILITY.PUBLIC);

        mockMvc.perform(put("/api/journal/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Journal updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Journal"));
    }

    @Test
    @WithMockUser
    void updateJournal_NotFound() throws Exception {
        when(journalServices.updateJournal(eq(999L), any(JournalRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/journal/999")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Journal not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void updateJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.updateJournal(eq(1L), any(JournalRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/api/journal/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== DELETE /api/journal/{id} ==========

    @Test
    @WithMockUser
    void deleteJournal_Success() throws Exception {
        when(journalServices.deleteJournalById(eq(1L), any(User.class)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/journal/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Journal deleted successfully"));
    }

    @Test
    @WithMockUser
    void deleteJournal_NotFound() throws Exception {
        when(journalServices.deleteJournalById(eq(999L), any(User.class)))
                .thenReturn(false);

        mockMvc.perform(delete("/api/journal/999")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Journal not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void deleteJournal_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(journalServices.deleteJournalById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(delete("/api/journal/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== Auth test ==========

    @Test
    void createJournal_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/journal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(journalRequest)))
                .andExpect(status().isUnauthorized());
    }
}

