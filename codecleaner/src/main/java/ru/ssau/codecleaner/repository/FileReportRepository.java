package ru.ssau.codecleaner.repository;

import ru.ssau.codecleaner.entity.FileReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileReportRepository extends JpaRepository<FileReport, Long> {
    List<FileReport> findByAnalysisId(Long analysisId);
}