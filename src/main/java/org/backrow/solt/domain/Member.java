package org.backrow.solt.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(value={AuditingEntityListener.class})
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private Integer birthYear;

    private Boolean gender;

    @CreatedDate
    @Column(name="regDate", updatable = false)
    private LocalDateTime regDate;

    @OneToOne(cascade = CascadeType.ALL,
    orphanRemoval = true,
    mappedBy = "member")
    private ProfileImage profileImage;

    public void changeMemberInfo(String password, String name, Integer birthYear) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
        this.name = name;
        this.birthYear = birthYear;
    }

    public void addImage(String uuid, String fileName){
        profileImage = ProfileImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .member(this)
                .build();
    }
}
