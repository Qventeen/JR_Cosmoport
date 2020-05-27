package com.space.service;


import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.repository.CosmoportRepository;

import com.space.service.utils.UtilShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация интерфейса CosmoportService
 */
@Service
@Transactional
public class CosmoportServiceImpl implements CosmoportService {

    private CosmoportRepository repository;

    //Утильный класс инкапсулирующий в себе необходимую бизнес логику
    private UtilShipService utilShipService;

    @Override
    public List<Ship> findAllShips(Integer pageSize, Integer pageNumber, ShipOrder shipOrder) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(shipOrder.getFieldName()));

        return repository.findAll(pageable).getContent();
    }

    @Override
    public Ship findShipById(Long id) {
        if(repository.existsById(id)) {
            return repository.findById(id).get();
        }
        return null;
    }

    @Override
    public List<Ship> findShipsByCriteria(Integer pageSize, Integer pageNumber, ShipOrder shipOrder, Specification<Ship> spec) {
        Pageable pageable =  PageRequest.of(pageNumber,pageSize, Sort.by(shipOrder.getFieldName()));

        List<Ship> shipList = repository.findAll(spec,pageable).getContent();
        return shipList;
    }

    @Override
    public Integer countShips(Specification<Ship> spec) {
        return (int) repository.count(spec);
    }

    @Override
    public Ship createShip(Ship ship) {
        //Выполняем операции округления и расчета рейтинга
        ship = utilShipService.shipPreparing(ship);

        //Сохраняем результаты в бд
        return repository.save(ship);
    }

    @Override
    public Ship updateShip(Ship ship) {
        //Если Id для обновления корабля в БД найден
        if(!repository.existsById(ship.getId()))
            return null;

        //Достаем этот корабль
        Ship oldShip = repository.getOne(ship.getId());

        //Выполняем редактирование корабля
        ship = utilShipService.shipMerge(oldShip, ship);

        //Записываем обновленный результат в БД
        ship = repository.save(ship);

        //Возвращаем результат обратно для отображения
        return ship;
    }

    @Override
    public boolean deleteShip(Long id) {
        //Если такого элемента в БД нет
        if(!repository.existsById(id))
            return false;

        //Иначе удаляем элемент и возвращаем подтверждение
        repository.deleteById(id);
        return true;
    }


    //Внедрение зависимостей

    @Autowired
    public void setUtilShipService(UtilShipService utilShipService) {
        this.utilShipService = utilShipService;
    }

    @Autowired
    public void setRepository(CosmoportRepository repository) {
        this.repository = repository;
    }



}
