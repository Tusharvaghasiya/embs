package org.project.sembs.evntmngmtbksytm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidatorConstraint implements ConstraintValidator<EnumValidator, CharSequence> { // specifies that it validates the CharSequence

    private Set<String> allowedValues;

    @Override // called when validator is initialized; used to extact enum values for efficient lookups
    public void initialize(EnumValidator constraintAnnotation) {
        allowedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override // acual validation happens here
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return allowedValues.contains(value.toString());
    }

}

