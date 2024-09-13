package org.backrow.solt.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
