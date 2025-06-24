package org.project.sembs.evntmngmtbksytm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Email(message = "Email must be a valid email address")
    private String email;

    @Size(min = 3, max = 100, message = "firstname must be between 3 and 100 characters")
    private String firstname;

    @Size(max = 100, message = "last name cannot exceed 100 characters")
    private String lastname;

}
