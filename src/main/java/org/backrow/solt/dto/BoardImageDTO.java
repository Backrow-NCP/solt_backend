package org.backrow.solt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardImageDTO {
    private String uuid;
    private String fileName;
    private int ord;
}
