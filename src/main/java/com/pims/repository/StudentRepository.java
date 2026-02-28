package com.pims.repository;

import com.pims.entity.Student;
import com.pims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser(User user);
}
