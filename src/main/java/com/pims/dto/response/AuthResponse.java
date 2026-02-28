package com.pims.dto.response;

import com.pims.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Role role;
    private boolean profileCompleted;
}
