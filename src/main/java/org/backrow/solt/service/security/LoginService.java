package org.backrow.solt.service.security;

import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.login.RegisterDTO;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    Boolean checkExist(String value, String type);

    boolean checkPassword(String email, String password);

    long login(LoginDTO loginDTO);

    boolean register(RegisterDTO registerDTO);

    String getToken(String email, Long memberId, String name);

    String getAuthUser(HttpServletRequest request);

    Long getMemberId(HttpServletRequest request);

    String getRefreshToken();

    void saveRefreshToken(String email, String refreshToken);
}
