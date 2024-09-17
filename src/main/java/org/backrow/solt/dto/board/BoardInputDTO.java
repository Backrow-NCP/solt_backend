package org.backrow.solt.dto.board;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class BoardInputDTO {
    @NotEmpty(message = "글 제목은 반드시 존재해야 합니다.")
    @Size(min = 1, max = 200, message = "글 제목은 1~200자 이내의 문자열이어야 합니다.")
    private String title;

    @NotEmpty(message = "본문은 반드시 존재해야 합니다.")
    @Size(min = 1, max = 5000, message = "본문은 1~5000자 이내의 문자열이어야 합니다.")
    private String content;

    @NotNull
    private Long memberId;

//    private PlanDTO plan;

    private Set<BoardImageDTO> boardImages = new HashSet<>();
}
