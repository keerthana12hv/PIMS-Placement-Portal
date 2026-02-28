package com.pims.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyDashboardResponse {

    private long totalJobs;
    private long openJobs;
    private long closedJobs;
    private long totalApplications;
}
