package org.backrow.solt.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceDTO {
    private Long placeId;

    @NotEmpty(message = "장소 이름은 반드시 입력되어야 합니다.")
    private String placeName;

    private String addr;

    private Integer price;

    private String description;

    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ss", example = "2024-10-21T05:42:37")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ss", example = "2024-10-21T05:42:37")
    private LocalDateTime endTime;

    @Builder.Default
    private boolean checker = false; // AI 판단 여부
}