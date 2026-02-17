package com.linea_desk.rest_linea.common.dto;

import jakarta.validation.constraints.*;

public class RegisterUserDto {
   
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 64, message = "Username must be between 2 and 64 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
 
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters") 
    private String password;
    private String githubUrl;

    public RegisterUserDto() { }

    public RegisterUserDto(
            String email,
            String username,
            String password,
            String githubUrl
    ) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.githubUrl = githubUrl;
    }

    public RegisterUserDto(
            String email,
            String username,
            String password
    ) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.githubUrl = null;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public RegisterUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public RegisterUserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public RegisterUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public RegisterUserDto setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
        return this;
    }
}
