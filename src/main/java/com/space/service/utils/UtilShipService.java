package com.space.service.utils;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Даный класс инкапсулирует в себе необходимую бизне логику
 */
@Component
public class UtilShipService {
    //Класс представляющий набор свойств приложения
    private ShipProperty pr;

    //Выполняет округление согласно параметров свойств приложения
    private Double roundCorrection(Double data){
        return new BigDecimal(data).setScale(pr.speedScale,pr.methodScale).doubleValue();
    }

    //Содержит логику подсчета рейтинга кораблей
    private Ship countRating(Ship ship){
        Calendar calendar = Calendar.getInstance();
        int y0 = pr.beforeProdYear;

        calendar.setTime(ship.getProdDate());
        int y1 = calendar.get(Calendar.YEAR);

        double k = ship.getUsed()? 0.5 : 1;
        double v = ship.getSpeed();

        Double rating = (80 * v * k ) / (y0 - y1+ 1);
        rating = roundCorrection(rating);
        ship.setRating(rating);

        return ship;
    }

    /**
     * Выполняет приведение корабля в готовность к добавлению в БД:
     *
     * округления чисел с плавающей запятой, соглано свойств приложения;
     * подсчет рейтинга корабля.
     *
     * @param ship - корабль для обработки
     * @return - подготовленный корабль
     */
    public Ship shipPreparing(Ship ship){
        //Выполняем коррекцию округления
        ship.setSpeed(roundCorrection(ship.getSpeed()));

        //Коррекция флага использования
        if(ship.getUsed() == null)
            ship.setUsed(false);

        //Выполняем расчет рейтинга
        ship = countRating(ship);

        return ship;
    }

    /**
     * Выполняет слияние параметров старого корабля с параметрами нового
     * @param oldShip - существующая версия корабля
     * @param newShip - набор дополнений для старого корабля
     * @return - обновленная версия старого корабля
     */
    public Ship shipMerge(Ship oldShip, Ship newShip){
        //Если параметр переданного корабля не Null и не пустой назначаем его новому кораблю
        if(newShip.getName() != null) oldShip.setName(newShip.getName());
        if(newShip.getPlanet() != null) oldShip.setPlanet(newShip.getPlanet());
        if(newShip.getShipType() != null) oldShip.setShipType(newShip.getShipType());
        if(newShip.getProdDate() != null) oldShip.setProdDate(newShip.getProdDate());
        if(newShip.getUsed() != null) oldShip.setUsed(newShip.getUsed());
        if(newShip.getSpeed() != null) oldShip.setSpeed(newShip.getSpeed());
        if(newShip.getCrewSize() != null) oldShip.setCrewSize(newShip.getCrewSize());

        //Выполняем стандартную коррекцию и пересчет рейтинга для сохранения
        shipPreparing(oldShip);

        //Возвращаем обновленное значение
        return oldShip;
    }

    //Внедрение зависимостей

    @Autowired
    public void setPr(ShipProperty pr) {
        this.pr = pr;
    }
}
