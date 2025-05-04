package app.quantun.backend.models.contract.validator.impl;

import app.quantun.backend.models.contract.validator.AtLeastOneFieldRequired;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;


@Slf4j
public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneFieldRequired, Object> {
    private String[] fields;

    @Override
    public void initialize(AtLeastOneFieldRequired constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
        log.debug("Initialized AtLeastOneFieldValidator with fields: {}", String.join(", ", fields));
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        log.debug("Validating object: {}", object);
        if (object == null) {
            log.warn("Validation failed: Object is null.");
            return false;
        }

        try {
            boolean atLeastOneFieldPresent = false;
            for (String fieldName : fields) {
                log.trace("Checking field: {}", fieldName);
                Field declaredField = object.getClass().getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);

                // Check if the field has a non-null value
                if (value != null) {
                    // For String type, also check if it's not empty
                    if (!(value instanceof String) || !((String) value).trim().isEmpty()) {
                        log.debug("Found non-null/non-empty value for field '{}': {}", fieldName, value);
                        atLeastOneFieldPresent = true;
                        break; // Found one, no need to check others
                    } else {
                        log.trace("Field '{}' is an empty string.", fieldName);
                    }
                } else {
                    log.trace("Field '{}' is null.", fieldName);
                }
            }

            if (!atLeastOneFieldPresent) {
                log.warn("Validation failed: None of the required fields ({}) were provided or non-empty.", String.join(", ", fields));
                // Custom error message
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "At least one of these fields must be provided: " + String.join(", ", fields))
                        .addConstraintViolation();
            } else {
                log.debug("Validation successful: At least one required field is present.");
            }

            return atLeastOneFieldPresent;
        } catch (NoSuchFieldException e) {
            log.error("Validation error: Field specified in annotation not found in the object class. {}", e.getMessage());
            // Optionally customize the error message for this specific exception
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Configuration error: Invalid field name specified for validation.")
                    .addConstraintViolation();
            return false; // Or handle as appropriate, maybe throw an exception
        } catch (Exception e) {
            log.error("Validation error during field access.{}", e.getMessage());
            // Generic error message for other reflection issues
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Internal validation error occurred.")
                    .addConstraintViolation();
            return false;
        }
    }
}