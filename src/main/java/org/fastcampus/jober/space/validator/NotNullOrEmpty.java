package org.fastcampus.jober.space.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotNullOrEmptyValidator.class)
public @interface NotNullOrEmpty {

    String message() default "Not null or empty violation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
