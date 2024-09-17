package org.backrow.solt.dto.page;

import lombok.Data;
import java.util.List;

@Data
public class PageResponseDTO<T> {
    private List<T> dataList;   // 해당 페이지의 데이터 목록
    private int totalPages;     // 총 페이지 수
    private long totalElements; // 전체 데이터 수
    private int currentPage;    // 현재 페이지 번호
    private int size;           // 한 페이지에 표시할 데이터 수

    public PageResponseDTO(List<T> dataList, int totalPages, long totalElements, int currentPage, int size) {
        this.dataList = dataList;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.size = size;
    }
}

