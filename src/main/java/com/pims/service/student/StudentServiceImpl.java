package com.pims.service.student;

import com.pims.dto.request.StudentProfileRequest;
import com.pims.dto.response.StudentProfileResponse;
import com.pims.entity.JobPosting;
import com.pims.entity.Student;
import com.pims.entity.User;
import com.pims.exception.ApiException;
import com.pims.repository.JobPostingRepository;
import com.pims.repository.StudentRepository;
import com.pims.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Path;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
            UserRepository userRepository,
            JobPostingRepository jobPostingRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
    }

    // 🔐 Get current logged in student
    private Student getCurrentStudent() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        return studentRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Student profile not found"));
    }

    // ================= CREATE / COMPLETE PROFILE =================
    @Override
    public void createProfile(StudentProfileRequest request) {

        Student student = getCurrentStudent();

        student.setUsn(request.getUsn());
        student.setFullName(request.getFullName());
        student.setBranch(request.getBranch());
        student.setCgpa(request.getCgpa());
        student.setGraduationYear(request.getGraduationYear());
        student.setCareerObjective(request.getCareerObjective());
        student.setSkills(request.getSkills());
        student.setLocation(request.getLocation());

        student.setProfileCompleted(true);

        studentRepository.save(student);
    }

    // ================= GET PROFILE =================
    @Override
    public StudentProfileResponse getProfile() {

        Student student = getCurrentStudent();

        StudentProfileResponse response = new StudentProfileResponse();
        response.setEmail(student.getUser().getEmail());
        response.setUsn(student.getUsn());
        response.setFullName(student.getFullName());
        response.setBranch(student.getBranch());
        response.setCgpa(student.getCgpa());
        response.setGraduationYear(student.getGraduationYear());
        response.setResumeUrl(student.getResumeUrl());
        response.setCareerObjective(student.getCareerObjective());
        response.setSkills(student.getSkills());
        response.setLocation(student.getLocation());

        return response;
    }

    // ================= UPDATE PROFILE =================
    @Override
    public void updateProfile(StudentProfileRequest request) {

        Student student = getCurrentStudent();

        student.setFullName(request.getFullName());
        student.setBranch(request.getBranch());
        student.setUsn(request.getUsn());
        student.setCgpa(request.getCgpa());
        student.setGraduationYear(request.getGraduationYear());
        student.setCareerObjective(request.getCareerObjective());
        student.setSkills(request.getSkills());
        student.setLocation(request.getLocation());

        studentRepository.save(student);
    }

    // ================= AVAILABLE JOBS =================
    // @Override
    // public List<JobPosting> getAvailableJobs() {
    //     return jobPostingRepository.findByStatus("OPEN");
    // }
     @Override
public List<JobPosting> getAvailableJobs() {

    Student student = getCurrentStudent();

    if (!Boolean.TRUE.equals(student.getProfileCompleted())) {
        throw new ApiException("Complete profile before viewing jobs");
    }

    return jobPostingRepository.findByStatus("OPEN");
}


    // @Override
    // public void uploadResume(MultipartFile file) {

    // Student student = getCurrentStudent();

    // try {

    // String uploadDir = "uploads/";
    // Path uploadPath = Paths.get(uploadDir);
    // Files.createDirectories(uploadPath);

    // // 🔥 Delete old resume if exists
    // if (student.getResumeUrl() != null) {
    // Path oldFilePath = Paths.get(student.getResumeUrl());
    // Files.deleteIfExists(oldFilePath);
    // }

    // String fileName = System.currentTimeMillis() + "_" +
    // file.getOriginalFilename();
    // Path filePath = uploadPath.resolve(fileName);

    // Files.write(filePath, file.getBytes());

    // student.setResumeUrl(uploadDir + fileName);
    // studentRepository.save(student);

    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new ApiException("Resume upload failed");
    // }
    // }

    // @Override
    // public void deleteResume() {

    // Student student = getCurrentStudent();

    // if (student.getResumeUrl() == null) {
    // throw new ApiException("No resume to delete");
    // }

    // try {

    // Path filePath = Paths.get(student.getResumeUrl());
    // Files.deleteIfExists(filePath);

    // student.setResumeUrl(null);
    // studentRepository.save(student);

    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new ApiException("Failed to delete resume");
    // }
    // }

    // ================= RESUME UPLOAD / DELETE =================}

    @Override
    public void uploadResume(MultipartFile file) {

        Student student = getCurrentStudent();

        try {
            // String uploadDir = "src/main/resources/static/uploads/";
            String uploadDir = "uploads/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // ✅ Store only relative URL
            student.setResumeUrl("uploads/" + fileName);

            studentRepository.save(student);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("Resume upload failed");
        }
    }

    @Override
    public void deleteResume() {

        Student student = getCurrentStudent();

        if (student.getResumeUrl() == null) {
            throw new ApiException("No resume to delete");
        }

        try {
            Path filePath = Paths.get("src/main/resources/static/")
                    .resolve(student.getResumeUrl());

            Files.deleteIfExists(filePath);

            student.setResumeUrl(null);
            studentRepository.save(student);

        } catch (Exception e) {
            throw new ApiException("Failed to delete resume");
        }
    }

}
