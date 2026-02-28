package com.pims.controller.company;

import com.pims.dto.request.JobPostingRequest;
import com.pims.service.jobPosting.JobPostingService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/jobs")
public class CompanyJobController {

    private final JobPostingService jobPostingService;

    public CompanyJobController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @PostMapping
    public String createJob(@Valid @RequestBody JobPostingRequest request) {
        jobPostingService.createJob(request);
        return "Job created successfully";
    }
}
