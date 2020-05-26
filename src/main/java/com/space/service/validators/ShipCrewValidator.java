package com.space.service.validators;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class ShipCrewValidator implements Validator {

    private ShipProperty pr;

    @Autowired
    public ShipCrewValidator(ShipProperty pr) {
        this.pr = pr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ship.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String lengthMessage = "Вне диапазона разрешенных значений";

        //Тестируем экипаж
        Integer crewSize = ((Ship) target).getCrewSize();
        if(pr.minCrewSize > crewSize || pr.maxCrewSize < crewSize) {
            errors.reject("Ship.crewSize", lengthMessage);
        }
    }
}
