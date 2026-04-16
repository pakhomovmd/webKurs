package ru.ssau.codecleaner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "dead_code_fragments")
public class DeadCodeFragment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "file_id", nullable = false)
    private FileReport fileReport;

    private Integer lineStart;
    private Integer lineEnd;

    @Column(length = 2000)
    private String codeSnippet;

    private String selectorOrFunction;
    private String reason;

    // Конструкторы
    public DeadCodeFragment() {}

    public DeadCodeFragment(FileReport fileReport, Integer lineStart, Integer lineEnd,
                            String codeSnippet, String selectorOrFunction, String reason) {
        this.fileReport = fileReport;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.codeSnippet = codeSnippet;
        this.selectorOrFunction = selectorOrFunction;
        this.reason = reason;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FileReport getFileReport() { return fileReport; }
    public void setFileReport(FileReport fileReport) { this.fileReport = fileReport; }

    public Integer getLineStart() { return lineStart; }
    public void setLineStart(Integer lineStart) { this.lineStart = lineStart; }

    public Integer getLineEnd() { return lineEnd; }
    public void setLineEnd(Integer lineEnd) { this.lineEnd = lineEnd; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }

    public String getSelectorOrFunction() { return selectorOrFunction; }
    public void setSelectorOrFunction(String selectorOrFunction) { this.selectorOrFunction = selectorOrFunction; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}