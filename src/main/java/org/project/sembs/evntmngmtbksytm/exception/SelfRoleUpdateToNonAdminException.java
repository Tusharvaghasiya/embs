package org.project.sembs.evntmngmtbksytm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class SelfRoleUpdateToNonAdminException extends RuntimeException {
    public SelfRoleUpdateToNonAdminException(String msg) {
        super(msg);
    }
}
