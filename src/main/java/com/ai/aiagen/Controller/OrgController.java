package com.ai.aiagen.Controller;

import com.ai.aiagen.Entity.Job;
import com.ai.aiagen.Repository.JobRepository;
import com.ai.aiagen.dto.JobRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/org")
@CrossOrigin(origins = "*")
public class OrgController {

    private final JobRepository jobRepository;

    public OrgController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // ✅ Add new Job Description
    @PostMapping("/add-jd")
    public ResponseEntity<?> addJobDescription(@RequestBody JobRequestDTO request) {

        if (request.getTitle() == null || request.getDescription() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "title and description are required"));
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());

        jobRepository.save(job);

        return ResponseEntity.ok(Map.of(
                "message", "Job description added successfully",
                "jobId", job.getId()
        ));
    }

    // ✅ Fetch all JDs
    @GetMapping("/all-jd")
    public List<Job> getAllJDs() {
        return jobRepository.findAll();
    }
}
