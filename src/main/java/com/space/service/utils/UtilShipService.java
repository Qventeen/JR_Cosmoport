package com.space.service.utils;

import com.space.model.Ship;
import com.space.service.ShipProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;

@Component
public class UtilShipService {

    private ShipProperty pr;

    private Double roundCorrection(Double data){
        return new BigDecimal(data).setScale(pr.speedScale,pr.methodScale).doubleValue();
    }

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

    public Ship shipAddCorrection(Ship ship){
        //Выполняем коррекцию округления
        ship.setSpeed(roundCorrection(ship.getSpeed()));

        //Коррекция флага использования
        if(ship.getUsed() == null)
            ship.setUsed(false);

        //Выполняем расчет рейтинга
        ship = countRating(ship);



        return ship;
    }

    public Ship shipMerge(Ship oldShip, Ship newShip){
        //Если параметр переданного корабля не Null и не пустой назначаем его новому кораблю
        if(newShip.getName() != null && !newShip.getName().isEmpty())
            oldShip.setName(newShip.getName());

        if(newShip.getPlanet() != null && !newShip.getPlanet().isEmpty())
            oldShip.setPlanet(newShip.getPlanet());

        if(newShip.getShipType() != null) oldShip.setShipType(newShip.getShipType());
        if(newShip.getProdDate() != null) oldShip.setProdDate(newShip.getProdDate());
        if(newShip.getUsed() != null) oldShip.setUsed(newShip.getUsed());
        if(newShip.getSpeed() != null) oldShip.setSpeed(newShip.getSpeed());
        if(newShip.getCrewSize() != null) oldShip.setCrewSize(newShip.getCrewSize());

        //Выполняем стандартную коррекцию и пересчет рейтинга для сохранения
        shipAddCorrection(oldShip);

        //Возвращаем обновленное значение
        return oldShip;
    }

    @Autowired
    public void setPr(ShipProperty pr) {
        this.pr = pr;
    }
}
