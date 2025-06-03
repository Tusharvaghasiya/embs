package org.project.sembs.evntmngmtbksytm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.project.sembs.evntmngmtbksytm.validation.StrongPassword;

@Getter
@Setter
public class UserRegistrationRequest {

    @NotBlank(message = "username cannot be blank")
    @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "email cannot exceed 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @StrongPassword
    private String password;

    @NotBlank(message = "firstname cannot be blank")
    @Size(min = 3, max = 100, message = "firstname must be between 3 and 100 characters")
    private String firstName;

    @NotBlank(message = "lastname cannot be blank")
    @Size(max = 100, message = "last name cannot exceed 100 characters")
    private String lastName;

}
