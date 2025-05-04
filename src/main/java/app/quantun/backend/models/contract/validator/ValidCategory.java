package app.quantun.backend.models.contract.validator;

import app.quantun.backend.models.contract.validator.impl.CategoryExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryExistsValidator.class)
public @interface ValidCategory {
    String message() default "Category does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
