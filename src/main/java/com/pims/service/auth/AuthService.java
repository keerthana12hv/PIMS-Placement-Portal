package com.pims.service.auth;

import com.pims.dto.request.LoginRequest;
import com.pims.dto.request.RegisterRequest;
import com.pims.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
