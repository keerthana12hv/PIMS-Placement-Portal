package com.pims.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String website;

    private String contactPerson;

    private String contactEmail;

    @Column(nullable = false)
    private boolean isApproved = false;

    @Column(name = "profile_completed")
    private Boolean profileCompleted = false;   // 🔥 NEW FIELD
}
