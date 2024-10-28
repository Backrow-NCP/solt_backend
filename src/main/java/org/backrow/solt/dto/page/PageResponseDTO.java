package org.backrow.solt.dto.page;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<T> {
    private final List<T> content; // 현재 페이지의 데이터 목록
    private final int currentPage; // 현재 페이지 번호
    private final int totalPages; // 총 페이지 수
    private final long totalElements; // 전체 데이터 수
    private final int size; // 페이지 크기
    private final boolean isFirst; // 첫 페이지 여부
    private final boolean isLast; // 마지막 페이지 여부

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(Page<T> page) { // Page 객체를 이용해 DTO 생성
        this.content = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }

    @Builder(builderMethodName = "withCustomContent")
    public PageResponseDTO(Page<?> page, List<T> content) {
        this.content = content;
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }
}
