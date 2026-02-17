package com.linea_desk.rest_linea.common.dto;

public class LoginResponse {
    private long id;
    private String email;
    private String jwtToken;
    private String username;

    public LoginResponse() { }
    
    public LoginResponse(
            String jwtToken, 
            long id,
            String email,
            String username
    ) {
        this.jwtToken = jwtToken;
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    
    public String getUsername() {
        return username;
    }
   
    public String getJwtToken() {
        return jwtToken;
    }

    public long getId() {
        return id;
    }

    public void SetId(long id) {
        this.id = id;
    }

    public void SetJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
