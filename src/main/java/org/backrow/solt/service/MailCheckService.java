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
        String title = "SoLOTrip(SOLT) 이메일 인증 번호";
        int code = 100000 + (int)(Math.random() * 899999);
        String authCode = String.valueOf(code);
        mailService.sendEmail(email, title, authCode);
        log.info("SAVE in Redis Server : "+authCode);
        redisService.setValues(AUTH_CODE_PREFIX + email,
                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    public boolean verifiedCode(String email, String authCode) {
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
        return authResult;
    }
}
