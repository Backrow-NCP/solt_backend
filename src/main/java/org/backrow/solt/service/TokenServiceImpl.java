package org.backrow.solt.service;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenServiceImpl implements TokenService {

    private RedisService redisService;

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
