package com.pims.controller.admin;

import com.pims.entity.Company;
import com.pims.exception.ApiException;
import com.pims.repository.CompanyRepository;
import org.springframework.web.bind.annotation.*;
import com.pims.dto.response.CompanyAdminResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CompanyRepository companyRepository;

    public AdminController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // 1️⃣ Get all companies
    @GetMapping("/companies")
    public List<CompanyAdminResponse> getAllCompanies() {
    return companyRepository.findAll()
            .stream()
            .map(company -> CompanyAdminResponse.builder()
                    .id(company.getId())
                    .companyName(company.getCompanyName())
                    .contactEmail(company.getContactEmail())
                    .approved(company.isApproved())
                    .build())
            .toList();
}

    // 2️⃣ Approve company
    @PutMapping("/company/{id}/approve")
    public String approveCompany(@PathVariable Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Company not found"));

        company.setApproved(true);
        companyRepository.save(company);

        return "Company approved successfully!";
    }

    // 3️⃣ Reject company
    @PutMapping("/company/{id}/reject")
    public String rejectCompany(@PathVariable Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Company not found"));

        company.setApproved(false);
        companyRepository.save(company);

        return "Company rejected successfully!";
    }
}
