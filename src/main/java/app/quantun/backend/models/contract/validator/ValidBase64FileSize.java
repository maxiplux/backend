package app.quantun.backend.models.contract.validator;

import app.quantun.backend.models.contract.validator.impl.Base64FileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = Base64FileSizeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBase64FileSize {
    String message() default "File size exceeds the maximum allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // This parameter specifies the maximum file size in bytes
    long maxSizeBytes() default 1024 * 1024; // Default 1MB
}