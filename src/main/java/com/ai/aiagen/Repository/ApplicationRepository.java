package com.ai.aiagen.Repository;

import com.ai.aiagen.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);
    List<Application> findByJobIdIn(List<Long> jobIds);
}
