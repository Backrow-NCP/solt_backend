package org.backrow.solt.service.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {

    public static void main(String[] args) {
        // BCryptPasswordEncoder 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 사용자가 입력한 비밀번호 (예시: 로그인할 때 입력한 비밀번호)
        String plainPassword = "superpower1!";  // 실제 비밀번호로 대체

        // 비밀번호를 새로 해시화하여 출력 (데이터베이스 저장 시 사용)
        String newHashedPassword = encoder.encode(plainPassword);
        System.out.println("새로 해시된 비밀번호: " + newHashedPassword);

        // 예시로 DB에서 가져온 해시된 비밀번호
        // 실제 테스트를 위해 일치하는 해시값을 새로 생성하여 사용해볼 수 있습니다.
        String hashedPasswordFromDB = "$2a$10$G0rqy0xydTj69brav7Y.jucpeauBqBpZG.SoNjpWCu0cRV3kJGGEK";

        // 비밀번호 비교
        if (encoder.matches(plainPassword, hashedPasswordFromDB)) {
            System.out.println("비밀번호가 일치합니다.");
        } else {
            System.out.println("비밀번호가 일치하지 않습니다.");
        }
    }
}
