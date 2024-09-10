package org.backrow.solt.dto.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardViewDTO {
        private Long boardId;
        private String title;
        private String content;
//        private UserDTO user;
//        private PlanDTO plan;
        private Integer likeCount;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
}
