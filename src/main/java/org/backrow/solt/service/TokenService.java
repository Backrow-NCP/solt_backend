package org.backrow.solt.service;

public interface TokenService {
    void saveRefreshToken(String email, String refreshToken, long expiration);

    String getRefreshToken(String email);

    void deleteRefreshToken(String email);

    boolean validateRefreshToken(String email, String refreshToken);
}
