package org.backrow.solt.dto.page;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<T> {
    private int page;
    private int size;
    private int total;

    private int startPage;
    private int endPage;

    private boolean prev;
    private boolean next;

    private List<T> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<T> dtoList, int total) {
        if(total <= 0) return;

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        this.endPage = (int)(Math.ceil(this.page / 10.0)) * 10;
        this.startPage = this.endPage - 9;

        int last = (int)(Math.ceil((total/(double)size)));
        this.endPage = Math.min(endPage, last);

        this.prev = this.startPage > 1;
        this.next = total > this.endPage * this.size;
    }
}

