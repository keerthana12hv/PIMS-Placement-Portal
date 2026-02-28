package com.pims.service.jobPosting;

import com.pims.dto.request.JobPostingRequest;
import com.pims.dto.response.JobResponse;
import com.pims.dto.response.CompanyDashboardResponse;
import java.util.*;

public interface JobPostingService {

    void createJob(JobPostingRequest request);
    List<JobResponse> getJobsForCompany();
    void closeJob(Long jobId);
    List<JobResponse> getAvailableJobsForStudent(int page, int size, String sortBy);
    void deleteJob(Long jobId);
    void autoCloseExpiredJobs();
    CompanyDashboardResponse getDashboardStats();

}
