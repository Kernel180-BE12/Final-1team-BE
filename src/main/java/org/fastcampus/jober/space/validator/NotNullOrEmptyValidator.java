package org.fastcampus.jober.space.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullOrEmptyValidator implements ConstraintValidator<NotNullOrEmpty, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !(value == null && !value.trim().isEmpty());
    }
}