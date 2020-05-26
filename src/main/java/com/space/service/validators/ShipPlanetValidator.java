package com.space.service.validators;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ShipPlanetValidator implements Validator {

    private ShipProperty pr;

    @Autowired
    public ShipPlanetValidator(ShipProperty pr) {
        this.pr = pr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ship.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String lengthMessage = "Вне диапазона разрешенных значений";

        //Проверка имени
        String str = ((Ship) target).getPlanet();
        if(str.length() < pr.minPlanetLength || str.length() > pr.maxPlanetLength) {
            errors.reject("Ship.planet", lengthMessage);
        }
    }
}
