package com.space.service.validators;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class ShipSpeedValidator implements Validator {

    private ShipProperty pr;

    @Autowired
    public ShipSpeedValidator(ShipProperty pr) {
        this.pr = pr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ship.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String lengthMessage = "Вне диапазона разрешенных значений";
        String emptyValue = "Задано пустое значение";
        //Тест скорости
        //Проба без округления
        Double speed = ((Ship) target).getSpeed();
        if(pr.minSpeed.compareTo(speed) > 0 || pr.maxSpeed.compareTo(speed) < 0){
            errors.reject("Ship.speed", lengthMessage);
        }
    }
}
