package com.pims.controller.company;

import com.pims.dto.request.CompanyProfileRequest;
import com.pims.dto.response.ApplicationResponse;
import com.pims.dto.response.CompanyDashboardResponse;
import com.pims.dto.response.CompanyProfileResponse;
import com.pims.dto.response.JobResponse;
import com.pims.enums.ApplicationStatus;
import com.pims.service.application.ApplicationService;
import com.pims.service.company.CompanyService;
import com.pims.service.jobPosting.JobPostingService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;
    private final CompanyService companyService;

    public CompanyController(JobPostingService jobPostingService,
                             ApplicationService applicationService,
                             CompanyService companyService) {
        this.jobPostingService = jobPostingService;
        this.applicationService = applicationService;
        this.companyService = companyService;
    }

    // ================= PROFILE =================

    @GetMapping("/profile")
    public CompanyProfileResponse getProfile() {
        return companyService.getProfile();
    }

    @PostMapping("/profile")
    public String createProfile(@RequestBody CompanyProfileRequest request) {
        companyService.createProfile(request);
        return "Company profile created successfully!";
    }

    @PutMapping("/profile")
    public String updateProfile(@RequestBody CompanyProfileRequest request) {
        companyService.updateProfile(request);
        return "Company profile updated successfully!";
    }

    // ================= JOBS =================

    @GetMapping("/jobs")
    public List<JobResponse> getCompanyJobs() {
        return jobPostingService.getJobsForCompany();
    }

    @PutMapping("/jobs/{jobId}/close")
    public String closeJob(@PathVariable Long jobId) {
        jobPostingService.closeJob(jobId);
        return "Job closed successfully";
    }

    @DeleteMapping("/jobs/{jobId}")
    public String deleteJob(@PathVariable Long jobId) {
        jobPostingService.deleteJob(jobId);
        return "Job deleted successfully!";
    }

    // ================= APPLICATIONS =================

    @GetMapping("/applications")
    public List<ApplicationResponse> getApplications() {
        return applicationService.getApplicationsForCompany();
    }

    @PutMapping("/applications/{id}")
    public String updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {

        applicationService.updateApplicationStatus(id, status);
        return "Application status updated successfully!";
    }

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public CompanyDashboardResponse getDashboard() {
        return jobPostingService.getDashboardStats();
    }
}
