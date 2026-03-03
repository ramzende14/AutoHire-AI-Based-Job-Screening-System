package com.ai.aiagen.Service;

import com.ai.aiagen.Entity.Job;
import com.ai.aiagen.Repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Get job description by jobId.
     *
     * @param jobId the ID of the job
     * @return job description if found, otherwise null
     */
    public String getJobDescriptionById(Long jobId) {
        if (jobId == null) {
            return null; // handle null jobId gracefully
        }

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        return optionalJob.map(Job::getDescription).orElse(null);
    }

    /**
     * Optional: fetch full Job entity if needed
     */
    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId).orElse(null);
    }
}
