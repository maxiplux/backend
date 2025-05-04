package app.quantun.backend.models.contract.validator.impl;

import app.quantun.backend.models.contract.request.ProductFilterDTO;
import app.quantun.backend.models.contract.validator.ValidCategory;
import app.quantun.backend.service.CategoryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryExistsValidator implements ConstraintValidator<ValidCategory, ProductFilterDTO> {

    private final CategoryService categoryService;

    public CategoryExistsValidator(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public boolean isValid(ProductFilterDTO filter, ConstraintValidatorContext context) {
        if (filter.getCategoryId() == null) {
            return true; // Skip validation if no category ID
        }

        if (!categoryService.existsById(filter.getCategoryId())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Category with ID " + filter.getCategoryId() + " does not exist")
                    .addPropertyNode("categoryId")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}