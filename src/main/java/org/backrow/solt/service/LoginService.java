package org.backrow.solt.service;

import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.login.RegisterDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface LoginService {

    Boolean checkExist(String value, String type);

    boolean checkPassword(String email, String password);

    long login(LoginDTO loginDTO);

    boolean register(RegisterDTO registerDTO);

    String getToken(String email);

    String getAuthUser(HttpServletRequest request);

    String getRefreshToken();

    void saveRefreshToken(String email, String refreshToken);

    long getMemberId(String email);
}
