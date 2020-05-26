package com.space.service;

import com.space.model.Ship;

import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Даный сервис выполняет построение набора спецификаций в зависимости от переданного набора
 * строковых параметров. В условии задачи определены возможные параметры запроса, поэтому
 * формируем критерий по каждому из них в виде отдельных методов.
 * Каждый метод принимает строковый параметр который уже внутри преобразует в нужный тип данных,
 * согласно критериям задания.
 * Метод build выполняет главную задачу построения цепочки спецификторов.
 * */

@Service
public class ShipCriteriaBuilder {

    /**
     * Хранимая переменная вычисленного набора фильтров
     */
    private Specification<Ship> filterShip = null;

    /**
     * Хранимый набор входных параметров для последнего вычисления
     */
    private Map<String, String> requestParams = null;

    /**
     * Мапа связи имени методов текущего класса с самими представлениями методов
     * Создается при конструировании
     */
    private Map<String, Method> methodMap;

    public ShipCriteriaBuilder() {
        this.methodMap = new HashMap<>();
        //Получаем все методы текущего класса
        Method[] thisMethods = this.getClass().getDeclaredMethods();

        //Создаем карту соответствия с именами методов в качестве ключей
        for(Method m: thisMethods){
            methodMap.put(m.getName(),m);
        }
    }

    /**
     * Основной метод построитель последовательности фильтров.
     * Адаптирован для множественного вызова с одинаковыми параметрами
     * @param rParams - Набор входных параметров в виде ключ-значение
     * @return - filter Для Ship
     * @throws Exception - Исключения в следствии некорретных знячений для входных параметров
     */
    public Specification<Ship> build(Map<String,String> rParams) {

        //Если входные параметры Null вернем пустой спецификатор
        if(rParams == null) return emptyFilter();

        //Если входные параметры еквивалентны тем, для которых вычисление уже проводилось -
        //вернем результат предыдущего вычисления
        if(requestParams != null && requestParams.equals(rParams))
            return filterShip;

        //Иначе создадим пустой фильтр
        //И проведем собственно вычисление
        requestParams = rParams;
        filterShip = emptyFilter();
        try {
            //В этом месте выполняем построение критериев по запросу
            for (Map.Entry<String, String> p : requestParams.entrySet()) {
                //Если в наборе методов есть имя эквивалентное критерию
                if (methodMap.containsKey(p.getKey())) {
                    //Получаем представление данного метода
                    Method method = methodMap.get(p.getKey());

                    //Выполняем вызов подходящего метода для текущего объекта
                    //со строковым параметром из запроса. Складываем вызов в цепочку
                    filterShip = filterShip
                            .and((Specification<Ship>) method.invoke(this, p.getValue()));
                }
            }
        } catch (Exception e) {
            System.out.println("Невалидное значение для какого-либо из входных параметров");
            return emptyFilter();
        }
        return filterShip;
    }


    //Все методы принимают тип String для универсальности вызова методов
    //Используем его. Конечно это костыль но другово варианта я пока не нашел.

    private Specification<Ship> name(String name){
        return (r,cq,cb)->  cb.like(r.get("name"), "%" + name + "%");
    }

    private Specification<Ship> planet(String planet){
        return (r,cq,cb) -> cb.like(r.get("planet"),"%" + planet + "%");
    }

    private Specification<Ship> shipType(String shipType){
        //В данном случае выполняем передачу по имени поскольку внутренне в БД
        //Тип корабля представлен в виде строки
        return (r,sq,cb)-> cb.equal(r.get("shipType"),ShipType.valueOf(shipType));
    }

    private Specification<Ship> after(String prodDate){
        Long req = Long.parseLong(prodDate);
        return (r,cq,cb)-> cb.greaterThanOrEqualTo(r.get("prodDate"), new Date(req));
    }

    private Specification<Ship> before(String prodDate){
        Long req = Long.parseLong(prodDate);
        return (r,cq,cb)-> cb.lessThanOrEqualTo(r.get("prodDate"), new Date(req));
    }

    private Specification<Ship> isUsed(String isUsed){
        Boolean req = Boolean.valueOf(isUsed);
        return (r,cq,cb) -> cb.equal(r.get("isUsed"),req);
    }

    private Specification<Ship> minSpeed(String speed){
        Double req = Double.parseDouble(speed);
        return (r,cq,cb) -> cb.greaterThanOrEqualTo(r.get("speed"), req);
    }

    private Specification<Ship> maxSpeed(String speed){
        Double req = Double.parseDouble(speed);
        return (r,cq,cb) -> cb.lessThanOrEqualTo(r.get("speed"), req);
    }

    private Specification<Ship> minCrewSize(String crewSize){
        Integer req = Integer.parseInt(crewSize);
        return (r,cq,cb) -> cb.greaterThanOrEqualTo(r.get("crewSize"), req);
    }

    private Specification<Ship> maxCrewSize(String crewSize){
        Integer req = Integer.parseInt(crewSize);
        return (r,cq,cb) -> cb.lessThanOrEqualTo(r.get("crewSize"), req);
    }

    private Specification<Ship> minRating(String rating){
        Double req = Double.parseDouble(rating);
        return (r,cq,cb) -> cb.greaterThanOrEqualTo(r.get("rating"), req);
    }

    private Specification<Ship> maxRating(String rating){
        Double req = Double.parseDouble(rating);
        return (r,cq,cb) -> cb.lessThanOrEqualTo(r.get("rating"), req);
    }

    //Необходимо вернуть пустой критерий на случай отсутствия критериев в запросе
    //Даный критерий должен возвращать все записи из таблицы.
    //Так как ID точно не Nul
    private Specification<Ship> emptyFilter(){
        return (r,cq,cb) -> cb.isNotNull(r.get("id"));
    }


}
