package com.space.controller;

import com.space.model.Ship;
import com.space.service.CosmoportService;
import com.space.service.ShipCriteriaBuilder;

import com.space.service.utils.UtilValidatorsServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/ships")
public class ShipRestController {

    //Сервис валидаторов
    private UtilValidatorsServis validatorsServis;

    //Сервис доступа к БД
    private CosmoportService cosmoport;

    //Сервис построения Фильтров
    private ShipCriteriaBuilder criteriaBuilder;

    //Получение данных по ID
    @RequestMapping(value = "/{id}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long shipId){

        //Если входной параметр не валидный
        if(!validatorsServis.idValidation(shipId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //Получаем корабль из БД
        Ship ship = cosmoport.findShipById(shipId);
        //Если корабль не найден
        if(ship == null){
            //Ответ = Страница не найдена
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Если корабль найден возвращаем его
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    //Получение данных по запросу
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getAllShips(
            @RequestParam(value = "pageSize",required = false, defaultValue = "3") Integer pageSize,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam Map<String, String> params){

        //Создаем пустой фильтр
        //Для корректной обработки базой данных
        Specification<Ship> specification = criteriaBuilder.build(params);

        List<Ship> shipList = cosmoport.findShipsByCriteria(pageSize, pageNumber, order, specification);

        return new ResponseEntity<List<Ship>>(shipList, HttpStatus.OK);
    }

    //Получение количества записей в БД по запросу
    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getCountShips(@RequestParam Map<String,String> params) {
        //Получаем фильтр по входным параметрам
        Specification<Ship> specification = criteriaBuilder.build(params);

        //Получаем количество записей в БД
        Integer countOfShips = cosmoport.countShips(specification);

        return new ResponseEntity<Integer>(countOfShips,HttpStatus.OK);

    }

    @RequestMapping(value = "/" , method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> postShip(@RequestBody Ship ship){

        if(!validatorsServis.newShipValidation(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ship = cosmoport.createShip(ship);
        return new ResponseEntity<>(ship,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id){
        //Создаем базовый статус
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if(validatorsServis.idValidation(id)){
            //Пытаемся выполнить удаление
            if (cosmoport.deleteShip(id))
                status = HttpStatus.OK;
            else
                status = HttpStatus.NOT_FOUND;
        }
        //Возвращаем статус операции
        return new ResponseEntity<>(status);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@RequestBody(required = false) Ship ship, @PathVariable("id") Long id){
        //Если id не валидный BAD_REQUEST
        if(!validatorsServis.idValidation(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //Иначе если тело запроса пустое вернуть корабль по id
        //TODO:: Обратить пристально внимание на даный элемент
        if(ship == null) {
            ship = cosmoport.findShipById(id);
            //Если элемент в БД не найден
            if(ship == null)
                //Уведомоляем что корабль не найден
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                //В противном случае просто возвращаем найденный корабль
                return new ResponseEntity<>(ship,HttpStatus.OK);
        }

        //Если корабль в теле запроса оказался не пустым.
        //Проверяем валиднойсть обновляемых данных
        if(!validatorsServis.updateShipValidation(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //Устанавливаем валидный ID
        ship.setId(id);

        //Выполняем обновления корабля в БД
        ship = cosmoport.updateShip(ship);

        //Если такой корабль не найден возвращаем что не найден
        if(ship == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //Иначе подтверждаем обновление и возвращаем его результат
        return new ResponseEntity<>(ship,HttpStatus.OK);
    }


    //Сервис
    @Autowired
    public void setCosmoport(CosmoportService service) {
        this.cosmoport = service;
    }

    //Фильтры
    @Autowired
    public void setCriteriaBuilder(ShipCriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    @Autowired
    public void setValidatorsServis(UtilValidatorsServis validatorsServis) {
        this.validatorsServis = validatorsServis;
    }
}


