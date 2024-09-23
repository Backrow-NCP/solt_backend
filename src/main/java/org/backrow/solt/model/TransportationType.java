package org.backrow.solt.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TransportationType {

    @Id
    private int id;
    private String type;

}
