package com.pims.entity;

import com.pims.enums.JobType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "job_postings")
public class JobPosting extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    private String location;

    private Double ctcOffered;

    private Integer positionsAvailable;

    private Double minCgpa;

    private String eligibleBranches;

    private LocalDate deadline;

    @Column(nullable = false)
    private String status = "OPEN";
}
