package ru.ssau.codecleaner.repository;

import ru.ssau.codecleaner.entity.AnalysisSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalysisSessionRepository extends JpaRepository<AnalysisSession, Long> {
    List<AnalysisSession> findByProjectId(Long projectId);
}