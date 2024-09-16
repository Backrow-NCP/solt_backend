package org.backrow.solt.dto.member;

import lombok.*;

import java.sql.Date;


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
