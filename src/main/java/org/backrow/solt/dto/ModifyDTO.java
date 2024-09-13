package org.backrow.solt.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ModifyDTO {

    private long memberId;

    private String password;

    private String name;

    private Integer birthYear;

}
