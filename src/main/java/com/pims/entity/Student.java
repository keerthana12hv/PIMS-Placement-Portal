package com.pims.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "students")

public class Student extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(unique = true)
    private String usn;

    private String fullName;

    private String branch;

    private Double cgpa;

    private Integer graduationYear;

    private String resumeUrl;

    @Column(name = "profile_completed")
    private Boolean profileCompleted = false;

    @Column(length = 1000)
    private String careerObjective;

    @Column(length = 500)
    private String skills;

    private String location;

}
