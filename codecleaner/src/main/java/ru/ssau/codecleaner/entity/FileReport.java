package ru.ssau.codecleaner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_reports")
public class FileReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "analysis_id", nullable = false)
    private AnalysisSession analysis;

    private String filePath;
    private Long totalSizeBytes;
    private Long unusedSizeBytes;
    private Double unusedPercentage;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @OneToMany(mappedBy = "fileReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeadCodeFragment> deadCodeFragments = new ArrayList<>();

    // Конструкторы
    public FileReport() {}

    public FileReport(AnalysisSession analysis, String filePath, Long totalSizeBytes,
                      Long unusedSizeBytes, Double unusedPercentage, FileType fileType) {
        this.analysis = analysis;
        this.filePath = filePath;
        this.totalSizeBytes = totalSizeBytes;
        this.unusedSizeBytes = unusedSizeBytes;
        this.unusedPercentage = unusedPercentage;
        this.fileType = fileType;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisSession getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisSession analysis) {
        this.analysis = analysis;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getTotalSizeBytes() {
        return totalSizeBytes;
    }

    public void setTotalSizeBytes(Long totalSizeBytes) {
        this.totalSizeBytes = totalSizeBytes;
    }

    public Long getUnusedSizeBytes() {
        return unusedSizeBytes;
    }

    public void setUnusedSizeBytes(Long unusedSizeBytes) {
        this.unusedSizeBytes = unusedSizeBytes;
    }

    public Double getUnusedPercentage() {
        return unusedPercentage;
    }

    public void setUnusedPercentage(Double unusedPercentage) {
        this.unusedPercentage = unusedPercentage;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public List<DeadCodeFragment> getDeadCodeFragments() {
        return deadCodeFragments;
    }

    public void setDeadCodeFragments(List<DeadCodeFragment> deadCodeFragments) {
        this.deadCodeFragments = deadCodeFragments;
    }
}