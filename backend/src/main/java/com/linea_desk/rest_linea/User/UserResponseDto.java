package com.linea_desk.rest_linea.User;

import lombok.Data;

@Data
public class UserResponseDto {
    private String email;
    private String username;

    public UserResponseDto() { }

    public UserResponseDto(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
