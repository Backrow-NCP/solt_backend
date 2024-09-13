package org.backrow.solt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterDTO {

    private String email;

    private String password;

    private String name;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthYear;

    private Boolean gender;

}
