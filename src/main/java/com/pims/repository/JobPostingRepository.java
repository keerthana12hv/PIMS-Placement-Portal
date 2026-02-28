package com.pims.repository;

import com.pims.entity.Company;
import com.pims.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatus(String status);

    List<JobPosting> findByCompany(Company company);

    Page<JobPosting> findByStatus(String status, Pageable pageable);

}
