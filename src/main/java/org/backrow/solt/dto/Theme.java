package org.backrow.solt.dto;

import lombok.Data;

import javax.persistence.Id;

@Data
public class Theme {

    @Id
    private String id;

    private String category;
}
