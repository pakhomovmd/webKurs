package ru.ssau.codecleaner.dto;

public class LoginResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;

    public LoginResponse(Long id, String email, String fullName, String role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}