package org.backrow.solt.domain.board;

import lombok.*;
import org.backrow.solt.domain.Member;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private BoardPlan boardPlan;

    @OneToMany(mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true) // boardImage를 지울 때, 파일이 삭제되도록 처리해야 함.
    @BatchSize(size = 10)
    private Set<BoardImage> boardImages = new HashSet<>();

    @OneToMany(mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private Set<LikeLog> likeLog = new HashSet<>();

    @OneToMany(mappedBy = "board",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Builder.Default
    private Set<Reply> replies = null;

    @CreatedDate
    @Column(name="regdate", updatable=false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name="moddate")
    private LocalDateTime modDate;

    public void modify(String title, String content, Set<BoardImage> boardImages) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (boardImages != null && !boardImages.isEmpty()) {
            this.boardImages.clear();
            this.boardImages.addAll(boardImages);
        }
    }
}
