package org.backrow.solt.dto.page;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PageRequestDTO {
    private int page;
    private int size;
    private Sort.Direction direction;
    private String sortBy;

    public PageRequestDTO() {
        this.page = 1; // 기본값 1페이지
        this.size = 10; // 기본값 한 페이지당 10개 항목
        this.direction = Sort.Direction.DESC; // 기본 정렬은 내림차순
        this.sortBy = "id"; // 기본 정렬 기준은 id
    }

    // 현재 페이지 요청에 맞는 Pageable 객체를 반환하는 메서드
    public org.springframework.data.domain.Pageable getPageable() {
        return org.springframework.data.domain.PageRequest.of(
                page - 1, size, Sort.by(direction, sortBy));
    }
}

