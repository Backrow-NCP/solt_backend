package org.backrow.solt.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
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

//    private Member member;
//    private BoardPlan boardPlan;

    @CreatedDate
    @Column(name="regdate", updatable=false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name="moddate")
    private LocalDateTime modDate;

    @OneToMany(mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true) // boardImage를 지울 때, 파일이 삭제되도록 처리해야 함.
    private List<BoardImage> boardImages;

    public void modify(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
    }
}
