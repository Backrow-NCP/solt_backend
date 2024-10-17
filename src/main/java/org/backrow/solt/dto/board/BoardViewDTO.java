package org.backrow.solt.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;

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

        private MemberInfoDTO member;

        private PlanViewDTO plan;

        private Set<BoardImageDTO> images;

        private Integer likeCount;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime regDate;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime modDate;
}
