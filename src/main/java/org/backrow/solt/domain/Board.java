package org.backrow.solt.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EntityListeners(value = {AuditingEntityListener.class})
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

//    private BoardPlan boardPlan; 플랜에 저장된 날짜보다 현재 일자가 나중이어야 함

    @OneToMany(mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true) // boardImage를 지울 때, 파일이 삭제되도록 처리해야 함.
    @BatchSize(size = 10)
    private List<BoardImage> boardImages = new ArrayList<>();

    @OneToMany(mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<LikeLog> likeLog = new ArrayList<>();

    @CreatedDate
    @Column(name="regdate", updatable=false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name="moddate")
    private LocalDateTime modDate;

    public void modify(String title, String content, List<BoardImage> boardImages) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (boardImages != null && !boardImages.isEmpty()) {
            this.boardImages.clear();
            this.boardImages.addAll(boardImages);
        }
    }
}
