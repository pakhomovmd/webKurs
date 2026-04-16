package ru.ssau.codecleaner.dto;

public class ProjectRequest {
    private String name;
    private String repoUrl;
    private String description;
    private Long userId;  // id пользователя, который создаёт проект

    // Конструкторы
    public ProjectRequest() {}

    public ProjectRequest(String name, String repoUrl, String description, Long userId) {
        this.name = name;
        this.repoUrl = repoUrl;
        this.description = description;
        this.userId = userId;
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}