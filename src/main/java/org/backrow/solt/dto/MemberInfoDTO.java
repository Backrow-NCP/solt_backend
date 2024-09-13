package org.backrow.solt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {

    private long memberId;
    private String name;
    private Date birthYear;
    private boolean gender;
    
    private String fileName;
}
