package com.linea_desk.rest_linea.Habit;

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
import com.linea_desk.rest_linea.Habit.Habit.HABIT_TYPE;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.exceptions.ResourceNotFoundException;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.ApplicationConfiguration;
import com.linea_desk.rest_linea.config.GithubOAuthSuccessHandler;
import com.linea_desk.rest_linea.config.JwtAuthenticationFilter;
import com.linea_desk.rest_linea.config.SecurityConfiguration;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(
    controllers = HabitControllers.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApplicationConfiguration.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GithubOAuthSuccessHandler.class)
    }
)
class HabitControllersTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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


    @Test
    @WithMockUser
    void createNewHabit_Success() throws Exception {
        when(habitServices.createNewHabit(any(HabitRequestDto.class), any(User.class)))
                .thenReturn(habitResponse);

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


    @Test
    @WithMockUser
    void getHabitById_Success() throws Exception {
        when(habitServices.getHabitById(eq(1L), any(User.class)))
                .thenReturn(habitResponse);

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
                .thenThrow(new ResourceNotFoundException("Habit", 999L));

        mockMvc.perform(get("/api/habit/999")
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
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


    @Test
    @WithMockUser
    void getHabitsList_Success() throws Exception {
        HabitResponseDto habit2 = new HabitResponseDto();
        habit2.setHabitName("Meditation");
        habit2.setType(HABIT_TYPE.MENTAL_WELLBEING);
        habit2.setStreaks(10);

        Collection<HabitResponseDto> habits = List.of(habitResponse, habit2);
        when(habitServices.getAllHabitsForUser(any(User.class)))
                .thenReturn(habits);

        mockMvc.perform(get("/api/habits")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Habits list retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
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


    @Test
    @WithMockUser
    void updateHabit_Success() throws Exception {
        HabitResponseDto updatedResponse = new HabitResponseDto();
        updatedResponse.setHabitName("Evening Run");
        updatedResponse.setType(HABIT_TYPE.FITNESS);
        updatedResponse.setStreaks(10);

        when(habitServices.updateHabit(eq(1L), any(HabitRequestDto.class), any(User.class)))
                .thenReturn(updatedResponse);

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
                .thenThrow(new ResourceNotFoundException("Habit", 999L));

        mockMvc.perform(put("/api/habit/999")
                        .with(csrf())
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
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


    @Test
    @WithMockUser
    void deleteHabit_Success() throws Exception {
        doNothing().when(habitServices).deleteHabitById(eq(1L), any(User.class));

        mockMvc.perform(delete("/api/habit/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteHabit_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Habit", 999L))
                .when(habitServices).deleteHabitById(eq(999L), any(User.class));

        mockMvc.perform(delete("/api/habit/999")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    void deleteHabit_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(habitServices).deleteHabitById(eq(1L), any(User.class));

        mockMvc.perform(delete("/api/habit/1")
                        .with(csrf())
                        .with(user(testUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    void createNewHabit_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/habit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitRequest)))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(
                        (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    )
                );
            return http.build();
        }
    }
}

