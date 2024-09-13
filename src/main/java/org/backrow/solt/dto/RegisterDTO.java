package org.backrow.solt.dto;

import lombok.*;
import org.backrow.solt.domain.ProfileImage;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterDTO {

    private String email;

    private String password;

    private String name;

    private Integer birthYear;

    private Boolean gender;

}
