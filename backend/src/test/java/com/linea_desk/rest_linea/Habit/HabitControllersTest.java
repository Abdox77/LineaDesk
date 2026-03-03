package com.linea_desk.rest_linea.Habit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linea_desk.rest_linea.Habit.Habit.HABIT_TYPE;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = HabitControllers.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class)
)
class HabitControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HabitServices habitServices;

    @MockitoBean
    private JwtService jwtService;

    private User testUser;
    private HabitRequestDto habitRequest;
    private HabitResponseDto habitResponse;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "password123");

        habitRequest = new HabitRequestDto();
        habitRequest.setHabitName("Morning Run");
        habitRequest.setType(HABIT_TYPE.FITNESS);
        habitRequest.setStreaks(5);

        habitResponse = new HabitResponseDto();
        habitResponse.setHabitName("Morning Run");
        habitResponse.setType(HABIT_TYPE.FITNESS);
        habitResponse.setStreaks(5);
    }

    // ========== POST /api/habit ==========

    @Test
    @WithMockUser
    void createNewHabit_Success() throws Exception {
        when(habitServices.createNewHabit(any(HabitRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(habitResponse));

        mockMvc.perform(post("/api/habit")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Habit created successfully"))
                .andExpect(jsonPath("$.data.habitName").value("Morning Run"))
                .andExpect(jsonPath("$.data.type").value("FITNESS"))
                .andExpect(jsonPath("$.data.streaks").value(5));
    }

    @Test
    @WithMockUser
    void createNewHabit_ServiceReturnsEmpty_ReturnsInternalServerError() throws Exception {
        when(habitServices.createNewHabit(any(HabitRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/habit")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }

    @Test
    @WithMockUser
    void createNewHabit_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(habitServices.createNewHabit(any(HabitRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/habit")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/habit/{id} ==========

    @Test
    @WithMockUser
    void getHabitById_Success() throws Exception {
        when(habitServices.getHabitById(eq(1L), any(User.class)))
                .thenReturn(Optional.of(habitResponse));

        mockMvc.perform(get("/api/habit/1")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Habit search was a success"))
                .andExpect(jsonPath("$.data.habitName").value("Morning Run"));
    }

    @Test
    @WithMockUser
    void getHabitById_NotFound() throws Exception {
        when(habitServices.getHabitById(eq(999L), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/habit/999")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Habit not found"));
    }

    @Test
    @WithMockUser
    void getHabitById_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(habitServices.getHabitById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/habit/1")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== GET /api/habits ==========

    @Test
    @WithMockUser
    void getHabitsList_Success() throws Exception {
        HabitResponseDto habit2 = new HabitResponseDto();
        habit2.setHabitName("Meditation");
        habit2.setType(HABIT_TYPE.MENTAL_WELLBEING);
        habit2.setStreaks(10);

        Collection<HabitResponseDto> habits = List.of(habitResponse, habit2);
        when(habitServices.getAllHabitsForUser(any(User.class)))
                .thenReturn(Optional.of(habits));

        mockMvc.perform(get("/api/habits")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Habits list retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser
    void getHabitsList_Empty_ReturnsNotFound() throws Exception {
        when(habitServices.getAllHabitsForUser(any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/habits")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No habits found for the user"));
    }

    @Test
    @WithMockUser
    void getHabitsList_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(habitServices.getAllHabitsForUser(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/habits")
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== PUT /api/habit/{id} ==========

    @Test
    @WithMockUser
    void updateHabit_Success() throws Exception {
        HabitResponseDto updatedResponse = new HabitResponseDto();
        updatedResponse.setHabitName("Evening Run");
        updatedResponse.setType(HABIT_TYPE.FITNESS);
        updatedResponse.setStreaks(10);

        when(habitServices.updateHabit(eq(1L), any(HabitRequestDto.class), any(User.class)))
                .thenReturn(Optional.of(updatedResponse));

        HabitRequestDto updateRequest = new HabitRequestDto();
        updateRequest.setHabitName("Evening Run");
        updateRequest.setStreaks(10);

        mockMvc.perform(put("/api/habit/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Habit updated successfully"))
                .andExpect(jsonPath("$.data.habitName").value("Evening Run"))
                .andExpect(jsonPath("$.data.streaks").value(10));
    }

    @Test
    @WithMockUser
    void updateHabit_NotFound() throws Exception {
        when(habitServices.updateHabit(eq(999L), any(HabitRequestDto.class), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/habit/999")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Habit not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void updateHabit_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(habitServices.updateHabit(eq(1L), any(HabitRequestDto.class), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/api/habit/1")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== DELETE /api/habit/{id} ==========

    @Test
    @WithMockUser
    void deleteHabit_Success() throws Exception {
        when(habitServices.deleteHabitById(eq(1L), any(User.class)))
                .thenReturn(true);

        mockMvc.perform(delete("/api/habit/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Habit deleted successfully"));
    }

    @Test
    @WithMockUser
    void deleteHabit_NotFound() throws Exception {
        when(habitServices.deleteHabitById(eq(999L), any(User.class)))
                .thenReturn(false);

        mockMvc.perform(delete("/api/habit/999")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Habit not found or inaccessible"));
    }

    @Test
    @WithMockUser
    void deleteHabit_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(habitServices.deleteHabitById(eq(1L), any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(delete("/api/habit/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== Auth test ==========

    @Test
    void createNewHabit_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/habit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isUnauthorized());
    }
}

