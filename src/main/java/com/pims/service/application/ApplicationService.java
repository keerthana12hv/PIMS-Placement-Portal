package com.pims.service.application;

import com.pims.dto.response.ApplicationResponse;
import com.pims.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    void applyToJob(Long jobId);
    List<ApplicationResponse> getApplicationsForCompany();
    void updateApplicationStatus(Long applicationId, ApplicationStatus status);
    List<ApplicationResponse> getApplicationsForStudent();

}
