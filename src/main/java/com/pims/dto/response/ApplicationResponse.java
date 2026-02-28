package com.pims.dto.response;

import java.time.LocalDateTime;

import com.pims.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationResponse {

    private Long applicationId;

    private String studentName;
    private String studentEmail;

    private String jobTitle;
    private String companyName; 

    private ApplicationStatus status;
    private LocalDateTime appliedDate;
}
