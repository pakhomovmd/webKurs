package ru.ssau.codecleaner.service;

import org.springframework.stereotype.Service;
import ru.ssau.codecleaner.dto.ProjectRequest;
import ru.ssau.codecleaner.entity.Project;
import ru.ssau.codecleaner.entity.User;
import ru.ssau.codecleaner.repository.ProjectRepository;
import ru.ssau.codecleaner.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Project createProject(ProjectRequest request) {
        // Используем геттеры, а не прямые поля
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Project project = new Project();
        project.setName(request.getName());
        project.setRepoUrl(request.getRepoUrl());
        project.setDescription(request.getDescription());
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());
        project.setIsArchived(false);

        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}