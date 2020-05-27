package com.space.service.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Валидатор для валидации полученного ID
 */
@Component
public class IdValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Long.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Long id = (Long) target;
        if(id == null || id <= 0)
            errors.reject("id", "Значение идентификатора невалидное");
    }
}
