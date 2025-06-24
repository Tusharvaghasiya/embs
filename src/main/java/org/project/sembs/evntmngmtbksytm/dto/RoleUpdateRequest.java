package org.project.sembs.evntmngmtbksytm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.project.sembs.evntmngmtbksytm.model.Role;
import org.project.sembs.evntmngmtbksytm.validation.EnumValidator;

@Getter
@Setter
public class RoleUpdateRequest {

    @EnumValidator(enumClass = Role.class, message = "Invalid role value. Allowed values are: ORGANIZER, ATTENDEE, ADMIN")
    private Role role;
}
