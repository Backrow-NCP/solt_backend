package org.backrow.solt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {

    private int member_id;
    private String name;
    private int birth_year;
    private boolean gender;
    private ImageDTO image;
}
