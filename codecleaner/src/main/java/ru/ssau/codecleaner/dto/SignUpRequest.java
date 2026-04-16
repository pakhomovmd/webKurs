package ru.ssau.codecleaner.dto;

public class SignUpRequest {
    private String email;
    private String password;
    private String fullName;

    // Конструкторы
    public SignUpRequest() {}

    public SignUpRequest(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}