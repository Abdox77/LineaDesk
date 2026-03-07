package com.linea_desk.rest_linea.Habit;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path="/api")
public class HabitControllers {

    private final HabitServices habitServices;

    public HabitControllers(HabitServices habitServices) {
        this.habitServices = habitServices;
    }

    @PostMapping(path="/habit")
    public ResponseEntity<ApiResponse<?>> createNewHabit(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody HabitRequestDto request
    ) {
        HabitResponseDto responseDto = habitServices.createNewHabit(request, currentUser);
        ApiResponse<?> response = new ApiResponse<>(true, "Habit created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path="/habit/{id}")
    public ResponseEntity<ApiResponse<?>> getHabitById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        HabitResponseDto habit = habitServices.getHabitById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Habit search was a success", habit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/habits")
    public ResponseEntity<ApiResponse<?>> getHabitsList(
            @AuthenticationPrincipal User user
    ) {
        Collection<HabitResponseDto> habits = habitServices.getAllHabitsForUser(user);
        ApiResponse<?> response = new ApiResponse<>(true, "Habits list retrieved successfully", habits);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/habit/{id}")
    public ResponseEntity<ApiResponse<?>> updateHabit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody HabitRequestDto request
    ) {
        HabitResponseDto habit = habitServices.updateHabit(id, request, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Habit updated successfully", habit);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/habit/{id}")
    public ResponseEntity<ApiResponse<?>> deleteHabit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        habitServices.deleteHabitById(id, user);
        ApiResponse<?> response = new ApiResponse<>(true, "Habit deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
