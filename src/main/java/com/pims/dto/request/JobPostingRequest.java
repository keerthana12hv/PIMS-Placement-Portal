package com.pims.dto.request;

import com.pims.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobPostingRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private JobType jobType;

    private String location;

    private Double ctcOffered;

    private Integer positionsAvailable;

    private Double minCgpa;

    private String eligibleBranches;

    private LocalDate deadline;

    // getters & setters
}
