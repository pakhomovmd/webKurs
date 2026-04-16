package ru.ssau.codecleaner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String repoUrl;
    private String description;  // <-- это поле должно быть
    private LocalDateTime createdAt;
    private Boolean isArchived = false;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisSession> analysisSessions = new ArrayList<>();

    // Конструкторы
    public Project() {}

    public Project(String name, String repoUrl, String description, User owner) {
        this.name = name;
        this.repoUrl = repoUrl;
        this.description = description;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
        this.isArchived = false;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public String getDescription() { return description; }  // <-- геттер
    public void setDescription(String description) { this.description = description; }  // <-- сеттер

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<AnalysisSession> getAnalysisSessions() { return analysisSessions; }
    public void setAnalysisSessions(List<AnalysisSession> analysisSessions) { this.analysisSessions = analysisSessions; }
}