package org.backrow.solt.service;

import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.login.RegisterDTO;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    Boolean checkExist(String value, String type);

    boolean checkPassword(String email, String password);

    int login(LoginDTO loginDTO);

    boolean register(RegisterDTO registerDTO);

    String getToken(String email);

    String getAuthUser(HttpServletRequest request);

    String getRefreshToken();

    void saveRefreshToken(String email, String refreshToken);
}
