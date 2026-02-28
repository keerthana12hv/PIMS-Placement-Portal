package com.pims.service.company;

import com.pims.dto.request.CompanyProfileRequest;
import com.pims.dto.response.CompanyProfileResponse;

public interface CompanyService {

    void createProfile(CompanyProfileRequest request);

    void updateProfile(CompanyProfileRequest request);

    CompanyProfileResponse getProfile();
}
