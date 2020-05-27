package com.space.service.utils;

import com.space.model.Ship;
import com.space.service.validators.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

/**
 * Даный сервис предоставляе услуги валидации информации о кораблях
 */
@Service
public class UtilValidatorsService {
    private IdValidator idValidator;
    private NullShipValidator nullShipValidator;
    private ShipCrewValidator shipCrewValidator;
    private ShipNameValidator shipNameValidator;
    private ShipPlanetValidator shipPlanetValidator;
    private ShipProdDatetValidator shipProdDateValidator;
    private ShipSpeedValidator shipSpeedValidator;

    /**
     * Выполняет валидацию параметров корабля согласно свойствам приложения
     * @param ship - корабль подлежащий валидации
     * @return - результат валидации
     */
    public boolean newShipValidation(Ship ship){
        DataBinder db = new DataBinder(ship);

        //Выполняем первый этап валидации
        db.setValidator(nullShipValidator);
        db.validate();

        //Если проверка процдена выполняем следующий этап валидации
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

        //Если все ок возвращаем подтверждение
        if(!db.getBindingResult().hasErrors()){
            return true;
        }

        //В случае нахождения ошибок выводим их в консоль ошибок
        db.getBindingResult().getAllErrors().forEach(System.err::println);
        return false;
    }

    /**
     * Выполняет валидацию переданного числа на предмет корректности использования
     * в качестве идентификатора в БД
     * @param id предположительный идентификатор
     * @return результат валидации
     */
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

    /**
     * Выполняет валидацию свойст корабля на предмет корректности
     * параметров для обновления информации о корабле в БД
     * @param ship модель корабля для валидации
     * @return результат валидации
     */
    public boolean updateShipValidation(Ship ship){
        //Добавляем валидаторы по мере нахождения непустых полей
        DataBinder db = new DataBinder(ship);
        if(ship.getName() != null) db.addValidators(shipNameValidator);
        if(ship.getPlanet() != null) db.addValidators(shipPlanetValidator);
        if(ship.getProdDate() != null) db.addValidators(shipProdDateValidator);
        if(ship.getSpeed() != null) db.addValidators(shipSpeedValidator);
        if(ship.getCrewSize() != null) db.addValidators(shipCrewValidator);

        //Непосредственно валидация
        db.validate();

        //Если все ок возвращаем подтверждение
        if(!db.getBindingResult().hasErrors()){
            return true;
        }

        //В случае нахождения ошибок выводим их в консоль ошибок
        db.getBindingResult().getAllErrors().forEach(System.err::println);
        return false;
    }

    //Внедрение зависимостей

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
