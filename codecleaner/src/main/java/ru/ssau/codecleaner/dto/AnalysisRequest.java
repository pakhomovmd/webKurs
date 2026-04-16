package ru.ssau.codecleaner.dto;

public class AnalysisRequest {
    private Long projectId;
    // файлы будут передаваться отдельно
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}