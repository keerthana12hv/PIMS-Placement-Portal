package com.pims.service.company;

import com.pims.dto.request.CompanyProfileRequest;
import com.pims.dto.response.CompanyProfileResponse;
import com.pims.entity.Company;
import com.pims.entity.User;
import com.pims.exception.ApiException;
import com.pims.repository.CompanyRepository;
import com.pims.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // 🔐 Get logged-in company
    private Company getCurrentCompany() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        return companyRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Company profile not found"));
    }

    // ================= COMPLETE PROFILE =================
    @Override
    public void createProfile(CompanyProfileRequest request) {

        Company company = getCurrentCompany();

        company.setCompanyName(request.getCompanyName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setContactPerson(request.getContactPerson());
        company.setContactEmail(request.getContactEmail());

        // 🔥 IMPORTANT
        company.setProfileCompleted(true);
        company.setApproved(false); // must be approved by admin

        companyRepository.save(company);
    }

    // ================= UPDATE PROFILE =================
    @Override
    public void updateProfile(CompanyProfileRequest request) {

        Company company = getCurrentCompany();

        company.setCompanyName(request.getCompanyName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setContactPerson(request.getContactPerson());
        company.setContactEmail(request.getContactEmail());

        companyRepository.save(company);
    }

    // ================= GET PROFILE =================
    @Override
    public CompanyProfileResponse getProfile() {

        Company company = getCurrentCompany();

        return new CompanyProfileResponse(
                company.getCompanyName(),
                company.getDescription(),
                company.getWebsite(),
                company.getContactPerson(),
                company.getContactEmail(),
                company.isApproved(),
                company.getProfileCompleted() 
        );
    }
}
