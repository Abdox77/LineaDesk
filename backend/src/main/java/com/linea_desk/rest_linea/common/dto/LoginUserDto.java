package com.linea_desk.rest_linea.common.dto;

public class LoginUserDto {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
   
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
