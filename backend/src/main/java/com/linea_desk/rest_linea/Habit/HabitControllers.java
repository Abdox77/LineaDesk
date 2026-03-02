package com.linea_desk.rest_linea.Habit;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.PathVariable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.ApiResponse;

@Log4j2
@RestController
@RequestMapping(path="/api")
public class HabitControllers {

    private final HabitServices habitServices;
    private static final Logger log = LogManager.getLogger(HabitControllers.class);

    public HabitControllers(HabitServices habitServices) {
        this.habitServices = habitServices;
    }

    @PostMapping(path="/habit")
    public ResponseEntity<ApiResponse<?>>
    createNewHabit(
            @AuthenticationPrincipal User currentUser,
            @RequestBody HabitRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<HabitResponseDto> habit = habitServices.createNewHabit(request, currentUser);
            if (habit.isEmpty()) {
                throw new Exception("invalid operation habit dto is empty");
            }

            HabitResponseDto responseDto = habit.get();
            response = new ApiResponse<>(
                    true,
                    "Habit created successfully",
                    responseDto
            );
        } catch (Exception e) {
            log.error("The exception was caught while trying to create new habit: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/habit/{id}")
    public ResponseEntity<ApiResponse<?>>
    getHabitById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            Optional<HabitResponseDto> habit = habitServices.getHabitById(id, user);

            if (habit.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Habit not found"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Habit search was a success",
                    habit.get()
            );
        } catch (Exception e) {
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/habits")
    public ResponseEntity<ApiResponse<?>>
    getHabitsList(
            @AuthenticationPrincipal User user
    ) {
        ApiResponse<?> response;

        try {
            Optional<Collection<HabitResponseDto>> habits = habitServices.getAllHabitsForUser(user);

            if (habits.isEmpty()) {
                log.error("No habits found for user with ID: {}", user.getUserId());
                response = new ApiResponse<>(
                        false,
                        "No habits found for the user"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Habits list retrieved successfully",
                    habits.get()
            );
        } catch (Exception e) {
            log.error("Error retrieving habits list: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/habit/{id}")
    public ResponseEntity<ApiResponse<?>>
    updateHabit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody HabitRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<HabitResponseDto> habit = habitServices.updateHabit(id, request, user);

            if (habit.isEmpty()) {
                response = new ApiResponse<>(
                        false,
                        "Habit not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Habit updated successfully",
                    habit.get()
            );
        } catch (Exception e) {
            log.error("Error updating habit: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/habit/{id}")
    public ResponseEntity<ApiResponse<?>>
    deleteHabit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        ApiResponse<?> response;

        try {
            boolean isDeleted = habitServices.deleteHabitById(id, user);
            if (!isDeleted) {
                response = new ApiResponse<>(
                        false,
                        "Habit not found or inaccessible"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse<>(
                    true,
                    "Habit deleted successfully"
            );
        } catch (Exception e) {
            log.error("Error deleting habit: {}", e.getMessage());
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}



