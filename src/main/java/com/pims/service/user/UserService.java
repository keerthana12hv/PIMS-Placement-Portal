package com.pims.service.user;

import com.pims.entity.User;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> findByEmail(String email);
}
