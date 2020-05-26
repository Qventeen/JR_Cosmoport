package com.space.service.utils;

import com.space.model.Ship;
import com.space.service.validators.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;


@Service
public class UtilValidatorsServis {
    private IdValidator idValidator;
    private NullShipValidator nullShipValidator;
    private ShipCrewValidator shipCrewValidator;
    private ShipNameValidator shipNameValidator;
    private ShipPlanetValidator shipPlanetValidator;
    private ShipProdDatetValidator shipProdDateValidator;
    private ShipSpeedValidator shipSpeedValidator;


    public boolean newShipValidation(Ship ship){
        DataBinder db = new DataBinder(ship);
        //Устанавливаем первый валидатор
        db.setValidator(nullShipValidator);
        db.validate();

        //Если проверка выполняем следующий этап валидации
        if(!db.getBindingResult().hasErrors()){
            db.replaceValidators(
                    shipNameValidator,
                    shipPlanetValidator,
                    shipProdDateValidator,
                    shipSpeedValidator,
                    shipCrewValidator
            );
            db.validate();
        }

        if(!db.getBindingResult().hasErrors()){
            return true;
        }

        //В случае нахождения ошибок выводим их в консоль ошибок
        db.getBindingResult().getAllErrors().forEach(System.err::println);
        return false;
    }

    public boolean idValidation(Long id){
        DataBinder db = new DataBinder(id);
        db.setValidator(idValidator);
        db.validate();
        if(db.getBindingResult().hasErrors()){
            db.getBindingResult().getAllErrors()
                    .forEach(System.err::println);
            return false;
        }

            return true;
    }

    public boolean updateShipValidation(Ship ship){
        //Добавляем валидаторы по мере нахождения непустых полей
        DataBinder db = new DataBinder(ship);
        if(ship.getName() != null) db.addValidators(shipNameValidator);
        if(ship.getPlanet() != null) db.addValidators(shipPlanetValidator);
        if(ship.getProdDate() != null) db.addValidators(shipProdDateValidator);
        if(ship.getSpeed() != null) db.addValidators(shipSpeedValidator);
        if(ship.getCrewSize() != null) db.addValidators(shipCrewValidator);

        db.validate();
        //Если после валидации найдены ошибки выводим в поток ошибок
        if(db.getBindingResult().hasErrors()) {
            db.getBindingResult()
                    .getAllErrors()
                    .forEach(System.err::println);
           return false;
        }

        //Валидация пройдена на отлично
        return true;
    }

    //Инициализация валидаторов

    @Autowired
    public void setIdValidator(IdValidator idValidator) {
        this.idValidator = idValidator;
    }

    @Autowired
    public void setNullShipValidator(NullShipValidator nullShipValidator) {
        this.nullShipValidator = nullShipValidator;
    }

    @Autowired
    public void setShipCrewValidator(ShipCrewValidator shipCrewValidator) {
        this.shipCrewValidator = shipCrewValidator;
    }

    @Autowired
    public void setShipNameValidator(ShipNameValidator shipNameValidator) {
        this.shipNameValidator = shipNameValidator;
    }

    @Autowired
    public void setShipPlanetValidator(ShipPlanetValidator shipPlanetValidator) {
        this.shipPlanetValidator = shipPlanetValidator;
    }

    @Autowired
    public void setShipProdDateValidator(ShipProdDatetValidator shipProdDatetValidator) {
        this.shipProdDateValidator = shipProdDatetValidator;
    }

    @Autowired
    public void setShipSpeedValidator(ShipSpeedValidator shipSpeedValidator) {
        this.shipSpeedValidator = shipSpeedValidator;
    }
}
