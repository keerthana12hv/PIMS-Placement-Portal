package com.pims.service.application;

import com.pims.dto.response.ApplicationResponse;
import com.pims.entity.*;
import com.pims.enums.ApplicationStatus;
import com.pims.exception.ApiException;
import com.pims.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            JobPostingRepository jobPostingRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    // ================= STUDENT APPLY =================
    @Override
    public void applyToJob(Long jobId) {


        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Student profile not found"));

        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found"));

        // 🚫 Prevent apply after deadline
        if (job.getDeadline() != null && job.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new ApiException("Application deadline has passed");
        }

        // 🚫 Prevent apply if CGPA less than required
        if (job.getMinCgpa() != null && student.getCgpa() != null) {

            if (student.getCgpa() < job.getMinCgpa()) {
                throw new ApiException("CGPA not eligible for this job");
            }
        }

        if (applicationRepository.findByStudentAndJob(student, job).isPresent()) {
            throw new ApiException("Already applied to this job");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        applicationRepository.save(application);
    }

    // ================= COMPANY VIEW APPLICATIONS =================
    @Override
    public List<ApplicationResponse> getApplicationsForCompany() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Company not found"));

        return applicationRepository.findByJobCompany(company)
                .stream()
                .map(app -> ApplicationResponse.builder()
                        .applicationId(app.getId())
                        .studentName(app.getStudent().getFullName())
                        .studentEmail(app.getStudent().getUser().getEmail())
                        .jobTitle(app.getJob().getTitle())
                        .status(app.getStatus())
                        .build())
                .toList();
    }

    @Override
    public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Company not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException("Application not found"));

        // 🔥 Security Check: Company can only update its own job applications
        if (!application.getJob().getCompany().getId().equals(company.getId())) {
            throw new ApiException("Not authorized to update this application");
        }

        application.setStatus(status);

        applicationRepository.save(application);
    }

    @Override
    public List<ApplicationResponse> getApplicationsForStudent() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Student profile not found"));

        return applicationRepository.findByStudent(student)
                .stream()
                .map(app -> ApplicationResponse.builder()
                        .applicationId(app.getId())
                        .studentName(student.getFullName())
                        .studentEmail(user.getEmail())
                        .jobTitle(app.getJob().getTitle())
                        .status(app.getStatus()) // ✅ FIXED
                        .appliedDate(app.getCreatedAt())
                        .companyName(
                        app.getJob()
                           .getCompany()
                           .getCompanyName()
                )
                        .build())
                .toList();
    }

}
