package com.pims.service.student;

import com.pims.dto.request.StudentProfileRequest;
import com.pims.dto.response.StudentProfileResponse;
import com.pims.entity.JobPosting;
import java.util.*;
import org.springframework.web.multipart.MultipartFile; 


public interface StudentService {

    void createProfile(StudentProfileRequest request);

    StudentProfileResponse getProfile();

    void updateProfile(StudentProfileRequest request);

     List<JobPosting> getAvailableJobs();

     void uploadResume(MultipartFile file);

     void deleteResume();

}
