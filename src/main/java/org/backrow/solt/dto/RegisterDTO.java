package org.backrow.solt.dto;

import lombok.*;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterDTO {

    private String email;

    private String password;

    private String name;

    private Integer birth_year;

    private Boolean gender;
}
