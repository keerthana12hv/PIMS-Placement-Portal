package com.pims.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileResponse {

    private String email;
    private String usn;
    private String fullName;
    private String branch;
    private Double cgpa;
    private Integer graduationYear;
    private String resumeUrl;
    private String careerObjective;
    private String skills;
    private String location;

}
