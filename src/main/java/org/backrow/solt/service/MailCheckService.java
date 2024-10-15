package org.backrow.solt.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MailCheckService{

    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    private final RedisService redisService;

    private final MailService mailService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public void sendCodeToEmail(String email) {
        log.info("1111111");
        String title = "SoLOTrip(SOLT) 이메일 인증 번호";
        log.info("222222");
        String authCode = this.createCode();
        log.info("33333333");
        mailService.sendEmail(email, title, authCode);
        log.info("SAVE in Redis Server : "+authCode);
        redisService.setValues(AUTH_CODE_PREFIX + email,
                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberService.createCode() exception occur");
            throw new RuntimeException(e);
        }
    }

    public boolean verifiedCode(String email, String authCode) {
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
        return authResult;
    }
}
