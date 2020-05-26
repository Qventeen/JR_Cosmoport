package com.space.service.validators;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ShipNameValidator implements Validator {

    private ShipProperty pr;

    @Autowired
    public ShipNameValidator(ShipProperty pr) {
        this.pr = pr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ship.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //Проверка имени
        String str = ((Ship) target).getName();
        if(str.length() < pr.minNameLength || str.length() > pr.maxNameLength) {
            String lengthMessage = "Вне диапазона разрешенных значений";
            errors.reject("Ship.name", lengthMessage);
        }
    }
}
