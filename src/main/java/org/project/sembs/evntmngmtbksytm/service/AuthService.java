package org.project.sembs.evntmngmtbksytm.service;

import org.project.sembs.evntmngmtbksytm.dto.UserRegistrationRequest;
import org.project.sembs.evntmngmtbksytm.exception.UserAlreadyExistsException;
import org.project.sembs.evntmngmtbksytm.model.Role;
import org.project.sembs.evntmngmtbksytm.model.User;
import org.project.sembs.evntmngmtbksytm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest registrationRequest) {
        // 1. Check for uniqueness
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + registrationRequest.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + registrationRequest.getEmail() + "' is already registered.");
        }

        // 2. Create new User entity
        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registrationRequest.getPassword())); // Hash the password
        newUser.setFirstName(registrationRequest.getFirstName());
        newUser.setLastName(registrationRequest.getLastName());

        // 4. Save the user
        return userRepository.save(newUser);
    }
}