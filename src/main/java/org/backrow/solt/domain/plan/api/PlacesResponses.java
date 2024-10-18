package org.backrow.solt.domain.plan.api;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlacesResponses {

    private Long placeId;           // 장소 ID
    private String placeName;       // 장소 이름
    private String addr;            // 주소
    private Integer price;          // 가격 (있다면)
    private LocalDateTime startTime; // 시작 시간
    private LocalDateTime endTime;   // 종료 시간
    private String description;     // 장소 설명
    private Boolean checker;        // 수정 가능 여부

    // 필요에 따라 추가적인 메서드도 여기에 작성할 수 있음.
}