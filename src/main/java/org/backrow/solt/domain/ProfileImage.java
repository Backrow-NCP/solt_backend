package org.backrow.solt.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfileImage {

    @Id
    private String uuid;

    private String fileName;

    private Integer ord;
}
