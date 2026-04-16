package ru.ssau.codecleaner.repository;

import ru.ssau.codecleaner.entity.DeadCodeFragment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeadCodeFragmentRepository extends JpaRepository<DeadCodeFragment, Long> {
    List<DeadCodeFragment> findByFileReportId(Long fileReportId);
}