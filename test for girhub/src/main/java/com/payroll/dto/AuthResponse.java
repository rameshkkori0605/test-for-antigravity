package com.payroll.dto;

public class AuthResponse {
    private boolean authenticated;
    private String email;
    private String fullName;
    private String role;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(boolean authenticated, String email, String fullName, String role, String message) {
        this.authenticated = authenticated;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
