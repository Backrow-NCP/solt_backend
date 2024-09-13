package org.backrow.solt.dto;

import lombok.*;
import org.backrow.solt.domain.ProfileImage;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterDTO {

    private String email;

    private String password;

    private String name;

    private Date birthYear;

    private Boolean gender;

}
