package com.pims.dto.response;

import com.pims.enums.JobType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    private Long id;

    private String companyName;

    private String title;

    private String description;

    private JobType jobType;

    private String location;

    private Double ctcOffered;

    private Integer positionsAvailable;

    private Double minCgpa;

    private String eligibleBranches;

    private LocalDate deadline;

    private String status;

    private boolean alreadyApplied;

}
