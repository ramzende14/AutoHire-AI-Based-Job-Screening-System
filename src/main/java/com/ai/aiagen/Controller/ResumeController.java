package com.ai.aiagen.Controller;

import com.ai.aiagen.Entity.ResumeInfo;
import com.ai.aiagen.Entity.User;
import com.ai.aiagen.Service.EmailService;
import com.ai.aiagen.Service.MatchService;
import com.ai.aiagen.Service.ResumeAnyService;
import com.ai.aiagen.Service.JobService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeAnyService resumeService;
    private final MatchService matchService;
    private final EmailService emailService;
    private final JobService jobService;      // NEW
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeController(
            ResumeAnyService resumeService,
            MatchService matchService,
            EmailService emailService,
            JobService jobService                // NEW
    ) {
        this.resumeService = resumeService;
        this.matchService = matchService;
        this.emailService = emailService;
        this.jobService = jobService;          // NEW
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobId") Long jobId,          // CHANGED
            @AuthenticationPrincipal User principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "User not authenticated"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No file uploaded"));
        }

        try {
            // STEP 1: Fetch Job Description using jobId
            String jobDescription = jobService.getJobDescriptionById(jobId);

            if (jobDescription == null || jobDescription.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid jobId or job description not found"));
            }

            // STEP 2: Extract Resume
            ResumeInfo savedResume = resumeService.extractAndSave(file);
            String resumeJson = savedResume.getExtractedJson();

            // STEP 3: ATS Match
            String atsResponse = matchService.matchResume(resumeJson, jobDescription);

            log.info("ATS Output: {}", atsResponse);

            JsonNode atsJson = objectMapper.readTree(atsResponse);
            int score = atsJson.get("overallScore").asInt();
            boolean shortlisted = score >= 80;

            // STEP 4: Email notifications
            if (shortlisted) {
                emailService.sendShortlistMail(
                        principal.getEmail(),
                        principal.getName(),
                        score
                );
            } else {
                emailService.sendRejectionMail(
                        principal.getEmail(),
                        principal.getName(),
                        score
                );
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Resume processed successfully",
                    "resumeId", savedResume.getId(),
                    "atsScore", score,
                    "shortlisted", shortlisted,
                    "atsJson", atsJson
            ));

        } catch (Exception e) {
            log.error("Resume processing failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong", "details", e.getMessage()));
        }
    }
}
