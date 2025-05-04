package app.quantun.backend.models.contract.validator.impl;

import app.quantun.backend.models.contract.validator.ValidBase64FileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Slf4j
@Component
public class Base64FileSizeValidator implements ConstraintValidator<ValidBase64FileSize, String> {

    private long maxSizeBytes;

    @Override
    public void initialize(ValidBase64FileSize constraintAnnotation) {
        this.maxSizeBytes = constraintAnnotation.maxSizeBytes();
        log.debug("Initialized Base64FileSizeValidator with max size: {} bytes", maxSizeBytes);
    }

    @Override
    public boolean isValid(String base64String, ConstraintValidatorContext context) {
        if (base64String == null || base64String.isEmpty()) {
            log.debug("Empty or null base64 string, skipping validation");
            return true; // Skip validation for null or empty strings
        }

        try {
            // Remove the "data:image/..." prefix if present
            String pureBase64 = base64String;
            if (base64String.contains(",")) {
                pureBase64 = base64String.split(",")[1];
                log.trace("Extracted pure base64 content without data URI prefix");
            }

            // Calculate decoded size
            int padding = 0;
            if (pureBase64.endsWith("==")) {
                padding = 2;
            } else if (pureBase64.endsWith("=")) {
                padding = 1;
            }

            // Formula to calculate original file size from Base64 string
            long fileSize = (pureBase64.length() * 3) / 4 - padding;
            log.debug("Calculated file size: {} bytes (max allowed: {} bytes)", fileSize, maxSizeBytes);

            if (fileSize > maxSizeBytes) {
                log.info("File size validation failed: {} bytes exceeds maximum of {} bytes",
                        fileSize, maxSizeBytes);
                return false;
            }

            log.debug("File size validation passed");
            return true;
        } catch (Exception e) {
            // If there's any error in calculation, consider it invalid
            log.error("Error during base64 file size validation", e);
            return false;
        }
    }
}