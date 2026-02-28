package com.pims.repository;

import com.pims.entity.Application;
import com.pims.entity.Company;
import com.pims.entity.JobPosting;
import com.pims.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByStudentAndJob(Student student, JobPosting job);
    List<Application> findByJobCompany(Company company);
    List<Application> findByStudent(Student student);
    boolean existsByJob(JobPosting job);
    boolean existsByStudentAndJob(Student student, JobPosting job);


}
