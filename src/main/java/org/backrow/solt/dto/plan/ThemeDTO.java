package org.backrow.solt.dto.plan;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeDTO {
    private Long themeId;
    private String name;
}
