package com.pims.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyProfileRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String description;

    private String website;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @Email(message = "Invalid contact email")
    private String contactEmail;
}
