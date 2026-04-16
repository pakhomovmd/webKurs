package ru.ssau.codecleaner.controller;

import ru.ssau.codecleaner.dto.ProjectRequest;
import ru.ssau.codecleaner.entity.Project;
import ru.ssau.codecleaner.entity.User;
import ru.ssau.codecleaner.repository.ProjectRepository;
import ru.ssau.codecleaner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PostMapping
    public Project createProject(@RequestBody ProjectRequest request) {
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Project project = new Project();
        project.setName(request.getName());
        project.setRepoUrl(request.getRepoUrl());
        project.setDescription(request.getDescription());  // description может быть null
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());
        project.setIsArchived(false);

        return projectRepository.save(project);
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}