package ru.ssau.codecleaner.service;

import ru.ssau.codecleaner.dto.ProjectRequest;
import ru.ssau.codecleaner.entity.Project;

import java.util.List;

public interface ProjectService {
    Project createProject(ProjectRequest request);
    List<Project> getAllProjects();
}