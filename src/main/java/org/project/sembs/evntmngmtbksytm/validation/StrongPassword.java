package org.project.sembs.evntmngmtbksytm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)

public @interface StrongPassword {
    String message() default "Password does not meet strength requirements.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
