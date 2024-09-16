package org.backrow.solt.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date birthYear;
    private boolean gender;
    
    private String fileName;
}
