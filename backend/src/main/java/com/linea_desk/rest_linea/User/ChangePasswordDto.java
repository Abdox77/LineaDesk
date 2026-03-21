package com.linea_desk.rest_linea.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordDto {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;

    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
}
