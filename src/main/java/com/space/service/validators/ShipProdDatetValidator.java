package com.space.service.validators;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;

@Component
public class ShipProdDatetValidator implements Validator {

    private ShipProperty pr;
    private Calendar calendar = Calendar.getInstance();

    @Autowired
    public ShipProdDatetValidator(ShipProperty pr) {
        this.pr = pr;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ship.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String lengthMessage = "Вне диапазона разрешенных значений";

        //Минимальный год выпуска
        //Вычисляем год производство текущего корабля
        calendar.setTime(((Ship) target).getProdDate());
        Integer year = calendar.get(Calendar.YEAR);
        Long date = calendar.getTimeInMillis();
        if(year < pr.afterProdYear || year > pr.beforeProdYear){
            errors.reject("Ship.prodDate", lengthMessage);
        }
        if(date < 0) errors.reject("Long.prodDate", "Значение < 0");

    }
}
