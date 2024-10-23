package org.backrow.solt.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    @Pattern(regexp = "t|c|w|tc|tw|twc", message = "잘못된 검색 종류입니다. 허용되는 값: t, c, w, tc, tw, twc")
    @Schema(description = "검색 종류: t (제목), c (내용), w (작성자), tc (제목+내용), tw (제목+작성자), twc (제목+내용+작성자)")
    private String type;

    private String keyword;

    @Pattern(regexp = "l", message = "잘못된 정렬 종류입니다. 허용되는 값: l (좋아요 수)")
    @Schema(description = "정렬 종류: l (좋아요 수)")
    private String order;

    @Schema(hidden = true)
    public String[] getTypes(){
        if(type==null || type.isEmpty())
            return null;
        return type.split("");
    }

    public Pageable getPageable(String...props){
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
    }
}
