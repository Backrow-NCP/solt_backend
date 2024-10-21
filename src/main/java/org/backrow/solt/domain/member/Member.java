package org.backrow.solt.domain.member;

import lombok.*;
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

    private String fileName;

    private LocalDateTime deleteDate;

    public void changeMemberInfo(String password, String name) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
        this.name = name;
    }

    // 회원탈퇴 시 회원의 id, 닉네임을 남겨놓고 나머지 값 제거
    // email과 password는 Null값이 될 수 없으므로 "NotExist"라는 String으로 변환
    public Member deleteMember() {
        this.email = "NotExist";
        this.password = "NotExist";
        this.birthYear = null;
        this.gender = null;
        this.fileName = null;
        this.deleteDate = LocalDateTime.now();
        return this;
    }

    public void deleteProfileImage() {
        this.fileName = null;
    }

    public void setProfileImage(String fileName) {
        this.fileName = fileName;
    }
}
