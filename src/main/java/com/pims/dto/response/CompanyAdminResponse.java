package com.pims.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyAdminResponse {

    private Long id;
    private String companyName;
    private String contactEmail;
    private boolean approved;
}