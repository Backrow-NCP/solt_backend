package org.backrow.solt.dto.member;

import lombok.*;
import org.backrow.solt.util.ValidationConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ModifyPasswordDTO {
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = ValidationConstants.PASSWORD_REGEX,
            message = "비밀번호는 8~20자 길이이며, 적어도 하나의 특수 문자를 포함해야 합니다.")
    private String password;
}
