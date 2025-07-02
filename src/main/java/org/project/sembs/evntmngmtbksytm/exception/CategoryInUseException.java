package org.project.sembs.evntmngmtbksytm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String msg) {
        super(msg);
    }
}
