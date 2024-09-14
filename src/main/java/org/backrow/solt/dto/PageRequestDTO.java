package org.backrow.solt.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; // 검색 종류: t, c, w, tc, tw, twc ...

    private String keyword;

    public String[] getTypes(){
        if(type==null || type.isEmpty())
            return null;
        return type.split("");
    }

    public Pageable getPageable(String...props){
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
    }
}
