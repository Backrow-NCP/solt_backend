package org.backrow.solt.model;

import javax.persistence.Entity;

@Entity
public class Route {

    private Place startPlace;
    private Place endPlace;
    private int price;
    private int transportId;
}
