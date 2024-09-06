package org.backrow.solt.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardDTO {
    private Long boardId;
    private String title;
    private String content;
//    private MemberDTO member;
//    private BoardPlanDTO boardPlan;
//    private Integer likeCount;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
