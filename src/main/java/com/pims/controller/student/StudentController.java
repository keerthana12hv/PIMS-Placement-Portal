package com.pims.controller.student;

import com.pims.dto.request.StudentProfileRequest;
import com.pims.dto.response.ApplicationResponse;
import com.pims.dto.response.JobResponse;
import com.pims.dto.response.StudentProfileResponse;
import com.pims.service.application.ApplicationService;
import com.pims.service.jobPosting.JobPostingService;
import com.pims.service.student.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;

    public StudentController(StudentService studentService, JobPostingService jobPostingService,
            ApplicationService applicationService) {
        this.studentService = studentService;
        this.jobPostingService = jobPostingService;
        this.applicationService = applicationService;
    }

    @PostMapping("/profile")
    public String createProfile(@Valid @RequestBody StudentProfileRequest request) {
        studentService.createProfile(request);
        return "Student profile created successfully!";
    }

    @GetMapping("/profile")
    public StudentProfileResponse getProfile() {
        return studentService.getProfile();
    }

    @PutMapping("/profile")
    public String updateProfile(@Valid @RequestBody StudentProfileRequest request) {
        studentService.updateProfile(request);
        return "Student profile updated successfully!";
    }

    @GetMapping("/jobs")
public List<JobResponse> getAvailableJobs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "deadline") String sortBy
) {
    return jobPostingService.getAvailableJobsForStudent(page, size, sortBy);
}

    @PostMapping("/apply/{jobId}")
    public String applyToJob(@PathVariable Long jobId) {
        applicationService.applyToJob(jobId);
        return "Applied successfully!";
    }

    @GetMapping("/applications")
public List<ApplicationResponse> getMyApplications() {
    return applicationService.getApplicationsForStudent();
}

@PostMapping("/upload-resume")
public String uploadResume(@RequestParam("file") MultipartFile file) {
    studentService.uploadResume(file);
    return "Resume uploaded successfully!";
}

@DeleteMapping("/delete-resume")
public String deleteResume() {
    studentService.deleteResume();
    return "Resume deleted successfully!";
}

}
