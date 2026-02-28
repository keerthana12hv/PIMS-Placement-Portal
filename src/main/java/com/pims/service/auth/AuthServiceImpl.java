package com.pims.service.auth;

import com.pims.dto.request.LoginRequest;
import com.pims.dto.request.RegisterRequest;
import com.pims.dto.response.AuthResponse;
import com.pims.entity.Company;
import com.pims.entity.Student;
import com.pims.entity.User;
import com.pims.enums.Role;
import com.pims.exception.ApiException;
import com.pims.repository.CompanyRepository;
import com.pims.repository.StudentRepository;
import com.pims.repository.UserRepository;
import com.pims.util.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           StudentRepository studentRepository,
                           CompanyRepository companyRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    
    // ================= LOGIN =================
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                // .orElseThrow(() -> new ApiException("User not found"));
                .orElseThrow(() -> new ApiException("Account not found. Please register first."));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        boolean profileCompleted = false;

        if (user.getRole() == Role.STUDENT) {

            Student student = studentRepository
                    .findByUser(user)
                    .orElseThrow(() -> new ApiException("Student profile not found"));

            profileCompleted = Boolean.TRUE.equals(student.getProfileCompleted());

        } else if (user.getRole() == Role.COMPANY) {

            Company company = companyRepository
                    .findByUser(user)
                    .orElseThrow(() -> new ApiException("Company profile not found"));

            profileCompleted = Boolean.TRUE.equals(company.getProfileCompleted());
        }

        return new AuthResponse(
                token,
                user.getRole(),
                profileCompleted
        );
    }

    // ================= REGISTER =================
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email already exists");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new ApiException("Admin registration not allowed");
        }

        // 1️⃣ Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        user = userRepository.save(user);

        // 2️⃣ Create Empty Profile
        if (request.getRole() == Role.STUDENT) {

            Student student = new Student();
            student.setUser(user);
            student.setProfileCompleted(false);

            studentRepository.save(student);

        } else if (request.getRole() == Role.COMPANY) {

            Company company = new Company();
            company.setUser(user);
            company.setCompanyName("TEMP_" + user.getEmail());
            company.setApproved(false);
            company.setProfileCompleted(false);

            companyRepository.save(company);
        }

        // 3️⃣ Generate Token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getRole(),
                false   // profile not completed after register
        );
    }
    
}
