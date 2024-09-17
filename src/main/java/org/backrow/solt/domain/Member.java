package org.backrow.solt.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.sql.Date;
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

    private Date birthYear;

    private Boolean gender;

    @CreatedDate
    @Column(name="regDate", updatable = false)
    private LocalDateTime regDate;

    @OneToOne(cascade = CascadeType.ALL,
    orphanRemoval = true,
    mappedBy = "member")
    private ProfileImage profileImage;

    private LocalDateTime deleteDate;

    public void changeMemberInfo(String password, String name, Date birthYear) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
        this.name = name;
        this.birthYear = birthYear;
    }

    public void addImage(String uuid, String fileName){
        profileImage = ProfileImage.builder()
                .uuid("s_" + uuid)
                .fileName(fileName)
                .member(this)
                .build();
    }

    // 회원탈퇴 시 회원의 id, 닉네임을 남겨놓고 나머지 값 제거
    public void deleteMember() {
        this.email = null;
        this.password = null;
        this.birthYear = null;
        this.gender = null;
        this.profileImage = null;
        this.deleteDate = LocalDateTime.now();
    }
}
