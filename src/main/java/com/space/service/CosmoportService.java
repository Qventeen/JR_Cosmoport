package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Интерфейс представляющий сервисный слой для работы
 * с репозиторием. Выполняет бизнес логику перед передачей
 * информации в БД
 */
public interface CosmoportService {

    /**
     * Даный метод возвращает все корабли без фильтрации
     * @return
     */
    List<Ship> findAllShips(Integer pageSize, Integer pageNumber, ShipOrder shipOrder);

    /**
     * Получает корабль по id
     * @param id
     * @return
     */
    Ship findShipById(Long id);

    /**
     * Даный метод возвращает список кораблей согласно заданных:
     * фильтра, порядка, номера страницы, и количества кораблей на страницу
     * @param pageSize - размер страницы
     * @param pageNumber - номер страницы
     * @param shipOrder - поле сортировки
     * @param spec - спецификация фильтрации
     * @return - отсортированный список кораблей согласно параметров
     */
    List<Ship> findShipsByCriteria(Integer pageSize, Integer pageNumber, ShipOrder shipOrder, Specification<Ship> spec);


    /**
     * Подсчитывает количество кораблей в базе данных которые соответствуют заданным критериям
     * @param spec - спецификации поиска
     * @return - количество найденных кораблей
     */
    Integer countShips(Specification<Ship> spec);

    /**
     * Вставляет новый корабль в базу данных
     * @param ship - Валидный экземпляр корабля
     */
    Ship createShip(Ship ship);

    /**
     * Обновляет существующий корабль в базе данных
     * @param ship
     */
    Ship updateShip(Ship ship);

    /**
     * Удаляет из базы экземпляр корабля по id
     * @param id - Идентификатор корабля в базе данных
     * @return
     */
    boolean deleteShip(Long id);






}
