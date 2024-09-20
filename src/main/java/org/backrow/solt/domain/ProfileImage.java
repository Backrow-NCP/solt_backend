package org.backrow.solt.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
@Builder
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    private String uuid;

    private String fileName;

    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;
}
