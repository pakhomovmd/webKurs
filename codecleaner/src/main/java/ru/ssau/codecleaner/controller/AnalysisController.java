package ru.ssau.codecleaner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.codecleaner.entity.*;
import ru.ssau.codecleaner.repository.AnalysisSessionRepository;
import ru.ssau.codecleaner.repository.FileReportRepository;
import ru.ssau.codecleaner.repository.ProjectRepository;
import ru.ssau.codecleaner.service.CodeAnalysisService;
import ru.ssau.codecleaner.repository.DeadCodeFragmentRepository;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalysisController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AnalysisSessionRepository analysisSessionRepository;

    @Autowired
    private CodeAnalysisService codeAnalysisService;

    @Autowired
    private FileReportRepository fileReportRepository;

    @Autowired
    private DeadCodeFragmentRepository deadCodeFragmentRepository;

    @PostMapping("/upload/{projectId}")
    public ResponseEntity<?> uploadAndAnalyze(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        AnalysisSession session = new AnalysisSession();
        session.setProject(project);
        session.setStartTime(LocalDateTime.now());
        session.setStatus(AnalysisStatus.RUNNING);
        analysisSessionRepository.save(session);

        try {
            codeAnalysisService.analyzeProject(file, session);

            session.setEndTime(LocalDateTime.now());
            session.setStatus(AnalysisStatus.COMPLETED);
            analysisSessionRepository.save(session);

            // Возвращаем простой объект, а не весь session
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Analysis completed");
            response.put("sessionId", session.getId());
            response.put("healthScore", session.getHealthScore());
            response.put("status", session.getStatus().name());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            session.setStatus(AnalysisStatus.FAILED);
            analysisSessionRepository.save(session);
            return ResponseEntity.status(500).body(Map.of("error", "Analysis failed: " + e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getAnalysisResult(@PathVariable Long sessionId) {
        AnalysisSession session = analysisSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<FileReport> reports = fileReportRepository.findByAnalysisId(sessionId);

        // Загружаем deadCodeFragments для каждого отчёта
        for (FileReport report : reports) {
            List<DeadCodeFragment> fragments = deadCodeFragmentRepository.findByFileReportId(report.getId());
            report.setDeadCodeFragments(fragments);
            System.out.println("Report " + report.getId() + " has " + fragments.size() + " fragments"); // отладка
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("status", session.getStatus().name());
        response.put("healthScore", session.getHealthScore());
        response.put("startTime", session.getStartTime());
        response.put("endTime", session.getEndTime());
        response.put("fileReports", reports);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    public List<AnalysisSession> getAnalysesByProject(@PathVariable Long projectId) {
        return analysisSessionRepository.findByProjectId(projectId);
    }
}