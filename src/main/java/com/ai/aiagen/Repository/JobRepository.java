package com.ai.aiagen.Repository;

import com.ai.aiagen.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByTitle(String title);
}
