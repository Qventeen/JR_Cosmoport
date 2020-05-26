package com.space.service.validators;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class NullShipValidator implements Validator {
    private ShipProperty pr;

    @Autowired
    public NullShipValidator(ShipProperty pr) {
        this.pr = pr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Ship.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Ship ship = (Ship) target;
        if(
           ship.getName() == null ||
           ship.getPlanet() == null ||
           ship.getProdDate() == null ||
           ship.getShipType() == null ||
           ship.getSpeed() == null ||
           ship.getCrewSize() == null
        )
            errors.reject("ship", "Имеет недопустимые Null поля");
    }

}
