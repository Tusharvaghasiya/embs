package org.project.sembs.evntmngmtbksytm.controller;


import jakarta.validation.Valid;
import org.project.sembs.evntmngmtbksytm.dto.JwtAuthenticationResponse;
import org.project.sembs.evntmngmtbksytm.dto.LoginRequest;
import org.project.sembs.evntmngmtbksytm.dto.UserRegistrationRequest;
import org.project.sembs.evntmngmtbksytm.dto.UserResponse;
import org.project.sembs.evntmngmtbksytm.model.User;
import org.project.sembs.evntmngmtbksytm.security.JwtTokenProvider;
import org.project.sembs.evntmngmtbksytm.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        User registeredUser = authService.registerUser(registrationRequest);
        UserResponse responseDto = UserResponse.fromUser(registeredUser);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userDetails.getUsername()));
    }
}