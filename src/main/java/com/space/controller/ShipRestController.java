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

/**
 * REST контроллер. Обрабатывает REST запросы клиента.
 */
@RestController
@RequestMapping("/rest/ships")
public class ShipRestController {

    //Сервис валидаторов
    private UtilValidatorsServis validatorsServise;

    //Сервис доступа к БД
    private CosmoportService cosmoport;

    //Сервис построения Фильтров
    private ShipCriteriaBuilder criteriaBuilder;

    /**
     * Принимает запрос на получение корабля по id
     * Возвращает:
     * json представление корабля из БД клиенту по id;
     * ответ 400 в случае если id не валидный;
     * ответ 404, если корабль в БД не найден
     * @param shipId - Идентификатор корабля в БД
     * @return - Ответ клиенту
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long shipId){

        //Если входной параметр не валидный
        if(!validatorsServise.idValidation(shipId)){
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

    /**
     * Выполняет обработку запроса на получение информации из БД
     * согласно критериев полученного запроса.
     * @param pageSize - Не обязательный параметр размера страницы. По умолчанию = 3
     * @param pageNumber - Не обязательный параметр номера страницы. По умолчанию = 0
     * @param order - Не обязательный параметр порядка сортировки ответа. По умолчанию = ID
     * @param params - Весь комплект полученных параметров для построения критериев поиска в БД
     * @return - Список кораблей отобранных согласно входящих параметров
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getAllShips(
            @RequestParam(value = "pageSize",required = false, defaultValue = "3") Integer pageSize,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam Map<String, String> params){

        //Получаем фильтр по входным параметрам
        Specification<Ship> specification = criteriaBuilder.build(params);

        //Отправляем запрос в БД
        List<Ship> shipList = cosmoport.findShipsByCriteria(pageSize, pageNumber, order, specification);

        return new ResponseEntity<List<Ship>>(shipList, HttpStatus.OK);
    }

    /**
     * Выполняет обработку на получение общего количества записей в БД согласно параметров запроса
     * @param params - Набор параметров фильтрации данных
     * @return - Найденное количество данных согласно запроса
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getCountShips(@RequestParam Map<String,String> params) {
        //Получаем фильтр по входным параметрам
        Specification<Ship> specification = criteriaBuilder.build(params);

        //Получаем количество записей в БД
        Integer countOfShips = cosmoport.countShips(specification);

        return new ResponseEntity<Integer>(countOfShips,HttpStatus.OK);
    }

    /**
     * Принимает и обрабатывает REST запрос на добавление нового корабля в БД
     * @param ship Получаем из json модель корабля для добавления в БД
     * @return Результат добавления
     */
    @RequestMapping(value = "/" , method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> postShip(@RequestBody Ship ship){
        //Проверяем на валидность данные нового корабля
        if(!validatorsServise.newShipValidation(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //Если данные валидны добавляем корабль в БД
        ship = cosmoport.createShip(ship);
        return new ResponseEntity<>(ship,HttpStatus.OK);
    }

    /**
     * Принимает и обрабатывает запрос на удаление корабля из БД
     * @param id идентификатор удаляемого корабля
     * @return результат выполнения операции
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id){
        //Создаем базовый статус
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if(validatorsServise.idValidation(id)){
            //Пытаемся выполнить удаление
            if (cosmoport.deleteShip(id))
                status = HttpStatus.OK;
            else
                status = HttpStatus.NOT_FOUND;
        }
        //Возвращаем статус операции
        return new ResponseEntity<>(status);
    }

    /**
     * Принимает и обрабатывает запрос на обновление корабля в БД
     * @param ship - Данные для обновления корабля в БД
     * @param id - Идентификатор обновляемого корабля
     * @return - Результат выполнения опреации
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@RequestBody(required = false) Ship ship, @PathVariable("id") Long id){
        //Если id не валидный BAD_REQUEST
        if(!validatorsServise.idValidation(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //Если корабль в теле запроса оказался не пустым.
        //Проверяем валиднойсть обновляемых данных
        if(!validatorsServise.updateShipValidation(ship))
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


    //Внедрение зависомостей

    @Autowired
    public void setCosmoport(CosmoportService service) {
        this.cosmoport = service;
    }


    @Autowired
    public void setCriteriaBuilder(ShipCriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    @Autowired
    public void setValidatorsServise(UtilValidatorsServis validatorsServise) {
        this.validatorsServise = validatorsServise;
    }
}


