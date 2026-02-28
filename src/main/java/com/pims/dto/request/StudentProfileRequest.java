package com.pims.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileRequest {

    @NotBlank
    private String usn;

    @NotBlank
    private String fullName;

    @NotBlank
    private String branch;

    private Double cgpa;

    private Integer graduationYear;

    private String careerObjective;
    private String skills;
    private String location;
}
