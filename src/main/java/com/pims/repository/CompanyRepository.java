package com.pims.repository;

import com.pims.entity.Company;
import com.pims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUser(User user);

}

