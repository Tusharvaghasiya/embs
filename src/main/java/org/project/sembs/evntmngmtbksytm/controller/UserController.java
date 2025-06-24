package org.project.sembs.evntmngmtbksytm.controller;

import jakarta.validation.Valid;
import org.project.sembs.evntmngmtbksytm.dto.RoleUpdateRequest;
import org.project.sembs.evntmngmtbksytm.dto.UserResponse;
import org.project.sembs.evntmngmtbksytm.dto.UserUpdateRequest;
import org.project.sembs.evntmngmtbksytm.model.Role;
import org.project.sembs.evntmngmtbksytm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentlyLoggedInUserDetails(Authentication authentication) {
        String username = authentication.getName();
        return userService.getCurrentlyLoggedInUserDetails(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUserDetails(Authentication authentication, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateUserDetails(username, userUpdateRequest));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRoleById(@PathVariable UUID userId, @RequestBody RoleUpdateRequest roleUpdateRequest, Authentication authentication) {
        String username = authentication.getName();
        UserResponse userResponse = userService.updateUserRoleById(userId, roleUpdateRequest.getRole(), username);
        return ResponseEntity.ok(userResponse);
    }
}
