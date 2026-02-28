package com.pims.service.jobPosting;

import com.pims.dto.request.JobPostingRequest;
import com.pims.entity.Company;
import com.pims.entity.JobPosting;
import com.pims.entity.Student;
import com.pims.entity.User;
import com.pims.exception.ApiException;
import com.pims.repository.ApplicationRepository;
import com.pims.repository.CompanyRepository;
import com.pims.repository.JobPostingRepository;
import com.pims.repository.StudentRepository;
import com.pims.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.pims.dto.response.CompanyDashboardResponse;
import com.pims.dto.response.JobResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class JobPostingServiceImpl implements JobPostingService {

        private final JobPostingRepository jobPostingRepository;
        private final CompanyRepository companyRepository;
        private final UserRepository userRepository;
        private final StudentRepository studentRepository;
        private final ApplicationRepository applicationRepository;

        public JobPostingServiceImpl(JobPostingRepository jobPostingRepository,
                        CompanyRepository companyRepository,
                        UserRepository userRepository,
                        StudentRepository studentRepository, ApplicationRepository applicationRepository) {
                this.jobPostingRepository = jobPostingRepository;
                this.companyRepository = companyRepository;
                this.userRepository = userRepository;
                this.studentRepository = studentRepository;
                this.applicationRepository = applicationRepository;
        }

        @Override
        public void createJob(JobPostingRequest request) {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not found"));

                Company company = companyRepository.findByUser(user)
                                .orElseThrow(() -> new ApiException("Company not found"));

                if (!company.isApproved()) {
                        throw new ApiException("Company is not approved by admin yet.");
                }

                JobPosting job = new JobPosting();
                job.setCompany(company);
                job.setTitle(request.getTitle());
                job.setDescription(request.getDescription());
                job.setJobType(request.getJobType());
                job.setLocation(request.getLocation());
                job.setCtcOffered(request.getCtcOffered());
                job.setPositionsAvailable(request.getPositionsAvailable());
                job.setMinCgpa(request.getMinCgpa());
                job.setEligibleBranches(request.getEligibleBranches());
                job.setDeadline(request.getDeadline());

                jobPostingRepository.save(job);
        }

        @Override
        public List<JobResponse> getAvailableJobsForStudent(int page, int size, String sortBy) {
                System.out.println("🔥 STUDENT JOB API CALLED");
                autoCloseExpiredJobs();

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not found"));

                Student student = studentRepository.findByUser(user)
                                .orElseThrow(() -> new ApiException("Student profile not found"));

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(sortBy).descending());

                Page<JobPosting> jobPage = jobPostingRepository.findByStatus("OPEN", pageable);

                return jobPage.getContent()
                                .stream()
                                .filter(job -> job.getMinCgpa() == null ||
                                                student.getCgpa() >= job.getMinCgpa())
                                .filter(job -> job.getEligibleBranches() == null ||
                                                job.getEligibleBranches().toLowerCase()
                                                                .contains(student.getBranch().toLowerCase()))

                                // .filter(job -> job.getEligibleBranches() == null ||
                                // job.getEligibleBranches().contains(student.getBranch()))
                                .filter(job -> job.getDeadline() == null ||
                                                job.getDeadline().isAfter(LocalDate.now()))
                                .map(job -> {

                                        boolean alreadyApplied = applicationRepository.existsByStudentAndJob(student,
                                                        job);

                                        return JobResponse.builder()
                                                        .id(job.getId())
                                                        .companyName(job.getCompany().getCompanyName())
                                                        .title(job.getTitle())
                                                        .description(job.getDescription())
                                                        .jobType(job.getJobType())
                                                        .location(job.getLocation())
                                                        .ctcOffered(job.getCtcOffered())
                                                        .positionsAvailable(job.getPositionsAvailable())
                                                        .minCgpa(job.getMinCgpa())
                                                        .eligibleBranches(job.getEligibleBranches())
                                                        .deadline(job.getDeadline())
                                                        .status(job.getStatus())
                                                        .alreadyApplied(alreadyApplied) // 🔥 directly here
                                                        .build();
                                })
                                .toList();
        }

        @Override
        public List<JobResponse> getJobsForCompany() {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not found"));

                Company company = companyRepository.findByUser(user)
                                .orElseThrow(() -> new ApiException("Company not found"));

                return jobPostingRepository.findByCompany(company)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private JobResponse mapToResponse(JobPosting job) {
                return JobResponse.builder()
                                .id(job.getId())
                                .companyName(job.getCompany().getCompanyName())
                                .title(job.getTitle())
                                .description(job.getDescription())
                                .jobType(job.getJobType())
                                .location(job.getLocation())
                                .ctcOffered(job.getCtcOffered())
                                .positionsAvailable(job.getPositionsAvailable())
                                .minCgpa(job.getMinCgpa())
                                .eligibleBranches(job.getEligibleBranches())
                                .deadline(job.getDeadline())
                                .status(job.getStatus())
                                .build();
        }

        @Override
        public void closeJob(Long jobId) {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not found"));

                Company company = companyRepository.findByUser(user)
                                .orElseThrow(() -> new ApiException("Company not found"));

                JobPosting job = jobPostingRepository.findById(jobId)
                                .orElseThrow(() -> new ApiException("Job not found"));

                // 🔥 Security Check
                if (!job.getCompany().getId().equals(company.getId())) {
                        throw new ApiException("Not authorized to close this job");
                }

                job.setStatus("CLOSED");

                jobPostingRepository.save(job);
        }

        @Override
        public void deleteJob(Long jobId) {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not found"));

                Company company = companyRepository.findByUser(user)
                                .orElseThrow(() -> new ApiException("Company not found"));

                JobPosting job = jobPostingRepository.findById(jobId)
                                .orElseThrow(() -> new ApiException("Job not found"));

                // 🔐 Ensure company owns job
                if (!job.getCompany().getId().equals(company.getId())) {
                        throw new ApiException("Not authorized to delete this job");
                }

                // 🚫 NEW RULE: Prevent delete if applications exist
                if (applicationRepository.existsByJob(job)) {
                        throw new ApiException("Cannot delete job. Students have already applied.");
                }

                jobPostingRepository.delete(job);
        }

        @Override
        public void autoCloseExpiredJobs() {

                List<JobPosting> jobs = jobPostingRepository.findByStatus("OPEN");

                jobs.stream()
                                .filter(job -> job.getDeadline() != null &&
                                                job.getDeadline().isBefore(java.time.LocalDate.now()))
                                .forEach(job -> {
                                        job.setStatus("CLOSED");
                                        jobPostingRepository.save(job);
                                });
        }

        @Override
        public CompanyDashboardResponse getDashboardStats() {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not found"));

                Company company = companyRepository.findByUser(user)
                                .orElseThrow(() -> new ApiException("Company not found"));

                long totalJobs = jobPostingRepository.findByCompany(company).size();

                long openJobs = jobPostingRepository
                                .findByCompany(company)
                                .stream()
                                .filter(job -> job.getStatus().equals("OPEN"))
                                .count();

                long closedJobs = jobPostingRepository
                                .findByCompany(company)
                                .stream()
                                .filter(job -> job.getStatus().equals("CLOSED"))
                                .count();

                long totalApplications = applicationRepository
                                .findByJobCompany(company)
                                .size();

                return CompanyDashboardResponse.builder()
                                .totalJobs(totalJobs)
                                .openJobs(openJobs)
                                .closedJobs(closedJobs)
                                .totalApplications(totalApplications)
                                .build();
        }

}
