package org.backrow.solt.dto;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ModifyDTO {

    private long memberId;

    private String password;

    private String name;

    private Date birthYear;

}