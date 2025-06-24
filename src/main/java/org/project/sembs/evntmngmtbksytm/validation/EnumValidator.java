package org.project.sembs.evntmngmtbksytm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidatorConstraint.class) // actual validation logic is implemented in EnumValidatorConstraint.class
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {

    Class<? extends Enum<?>> enumClass(); // holds Class Object of enum we want to validate

    String message() default "must be any of {enumClass}"; // default error message

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
