// com.example.demo.service.UserService.java
package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerNewUser(UserRegistrationDto dto) {
    	if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("すでにそのユーザー名は使われています");
        }
    	
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole()); // 例: "ROLE_STAFF"
        userRepository.save(user);
    }
}

