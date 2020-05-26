package com.space.service;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ShipProperty {
    public boolean emptyFlag = false;

    public Integer minNameLength = 1;
    public Integer maxNameLength = 50;

    public Integer minPlanetLength = 1;
    public Integer maxPlanetLength = 50;

    public Integer afterProdYear = 2800;
    public Integer beforeProdYear = 3019;

    public Boolean defaultIsUsed = false;
    public String nameIsUsed = "isUsed";

    public Integer speedScale = 2;
    public Integer methodScale = BigDecimal.ROUND_HALF_UP;

    public Double minSpeed = 0.01;
    public Double maxSpeed = 0.99;

    public Integer minCrewSize = 1;
    public Integer maxCrewSize = 9999;

    public Integer ratingScale = 2;
}
