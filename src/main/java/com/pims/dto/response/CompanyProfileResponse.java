package com.pims.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyProfileResponse {

    private String companyName;
    private String description;
    private String website;
    private String contactPerson;
    private String contactEmail;
    private boolean approved;
    private boolean profileCompleted; 
}
