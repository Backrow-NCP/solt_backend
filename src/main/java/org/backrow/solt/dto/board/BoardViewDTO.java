package org.backrow.solt.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.backrow.solt.dto.member.MemberInfoDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardViewDTO {
        private Long boardId;
        private String title;
        private String content;
//        private PlanDTO plan;
        private MemberInfoDTO member;
        private Set<BoardImageDTO> images;
        private Integer likeCount;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
}
