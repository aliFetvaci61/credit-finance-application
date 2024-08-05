package com.alifetvaci.credit.userservice.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidNameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        try {
            String pattern = "(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){0,24}$";
            return value.matches(pattern);
        } catch (Exception ex) {
            return false;
        }
    }
}
