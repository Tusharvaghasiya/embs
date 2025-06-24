package org.project.sembs.evntmngmtbksytm.service;

import org.project.sembs.evntmngmtbksytm.dto.UserResponse;
import org.project.sembs.evntmngmtbksytm.dto.UserUpdateRequest;
import org.project.sembs.evntmngmtbksytm.exception.SelfRoleUpdateToNonAdminException;
import org.project.sembs.evntmngmtbksytm.exception.UserAlreadyExistsException;
import org.project.sembs.evntmngmtbksytm.model.Role;
import org.project.sembs.evntmngmtbksytm.model.User;
import org.project.sembs.evntmngmtbksytm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Spring Security uses UserDetailsService to load user-specific data.


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Helper method used by JWT filter to load User by username (subject of JWT)
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)));

        // The role should be prefixed with "ROLE_" for Spring Security's default role processing
        // if you're using hasRole() checks. If using hasAuthority(), the prefix is not strictly needed but conventional.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Use username for UserDetails principal
                user.getPasswordHash(),
                user.isActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities);
    }

    public Optional<UserResponse> getCurrentlyLoggedInUserDetails(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponse::fromUser);
    }

    public UserResponse updateUserDetails(String username, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isUpdateReq = false;

        if (StringUtils.hasText(userUpdateRequest.getFirstname()) && !userUpdateRequest.getFirstname().equals(user.getUsername())) {
            user.setFirstName(userUpdateRequest.getFirstname());
            isUpdateReq = true;
        }

        if (StringUtils.hasText(userUpdateRequest.getLastname()) && !userUpdateRequest.getLastname().equals(user.getUsername())) {
            user.setLastName(userUpdateRequest.getLastname());
            isUpdateReq = true;
        }

        if (StringUtils.hasText(userUpdateRequest.getEmail()) && !userUpdateRequest.getEmail().equalsIgnoreCase(user.getEmail())) {
            Optional<User> emailUser = userRepository.findByEmail(userUpdateRequest.getEmail());
            if (emailUser.isPresent()) {
                throw new UserAlreadyExistsException("User already exists with specified email");
            }
            user.setEmail(userUpdateRequest.getEmail());
            isUpdateReq = true;
        }

        if (isUpdateReq) {
            return UserResponse.fromUser(userRepository.save(user));
        }

        return UserResponse.fromUser(user);
    }

    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::fromUser);
    }

    public UserResponse updateUserRoleById(UUID id, Role role, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getId().equals(id) && role != Role.ADMIN) {
            throw new SelfRoleUpdateToNonAdminException("Cannot update self's role to non admin");
        }

        User updatingUser = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updatingUser.setRole(role);
        return UserResponse.fromUser(userRepository.save(updatingUser));
    }
}
