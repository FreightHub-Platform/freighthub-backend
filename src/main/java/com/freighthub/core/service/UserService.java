// src/main/java/com/freighthub/core/service/UserService.java

package com.freighthub.core.service;

import com.freighthub.core.entity.User;
import com.freighthub.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    }
