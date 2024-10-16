package org.backrow.solt.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.service.redis.RedisService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Log4j2
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RedisService redisService;

    @Override
    public void saveRefreshToken(String email, String refreshToken, long expiration) {
        redisService.setValues(email, refreshToken, Duration.ofMillis(expiration));
    }

    @Override
    public String getRefreshToken(String email) {
        return redisService.getValues(email);
    }

    @Override
    public void deleteRefreshToken(String email) {
        redisService.deleteValues(email);
    }

    @Override
    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedToken = getRefreshToken(email);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}
