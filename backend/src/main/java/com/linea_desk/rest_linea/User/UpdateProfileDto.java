package com.linea_desk.rest_linea.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileDto {
    @Size(min = 2, message = "Username must be at least 2 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
