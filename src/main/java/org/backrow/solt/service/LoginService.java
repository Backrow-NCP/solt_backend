package org.backrow.solt.service;

import org.backrow.solt.dto.LoginDTO;
import org.backrow.solt.dto.RegisterDTO;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    Boolean checkExist(String value, String type);

    boolean checkPassword(String email, String password);

    int login(LoginDTO loginDTO);

    boolean register(RegisterDTO registerDTO);

    String getToken(String email);

    String getAuthUser(HttpServletRequest request);
}
