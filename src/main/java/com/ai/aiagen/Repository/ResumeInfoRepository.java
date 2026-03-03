package com.ai.aiagen.Repository;

import com.ai.aiagen.Entity.ResumeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeInfoRepository extends JpaRepository<ResumeInfo, Long> {

    List<ResumeInfo> findByUserId(Long userId);  // ✔ This is the only valid method

}
