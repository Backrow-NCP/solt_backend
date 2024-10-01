package org.backrow.solt.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.login.RegisterDTO;
import org.backrow.solt.repository.LoginRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class LoginServiceImpl implements LoginService {

    // 1000ms 단위라서 *1000해줘야 한다
    static final long EXPIRATION_TIME = 60*60*24*1000;
    static final String PREFIX = "Bearer ";
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final LoginRepository loginRepository;
    private final TokenService tokenService;

    @Override
    public Boolean checkExist(String value, String type) {
        switch (type) {
            case "email":
                String existEmail = loginRepository.checkEmail(value);
                return existEmail != null && !existEmail.isEmpty();
            case "name":
                String existName = loginRepository.checkName(value);
                return existName != null && !existName.isEmpty();
        }
        throw new RuntimeException("Errors on checking " + type + " = "+ value);
    }

    // 비밀번호 정확한지 확인가능
    @Override
    public boolean checkPassword(String email, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return (Boolean)encoder.matches(password, loginRepository.findPwByEmail(email));
    }

    // Security와는 별도로 돌아가는 인증
    @Override
    public int login(LoginDTO loginDTO) {
        String checkEmail = loginRepository.checkEmail(loginDTO.getEmail());
        String checkPW = loginDTO.getPassword();
        if(checkPassword(checkEmail, checkPW)) {
            return (int) loginRepository.findIdByEmail(loginDTO.getEmail());
        }
        throw new RuntimeException("No Password found for email: " + loginDTO.getEmail());
    }

    @Override
    public boolean register(RegisterDTO registerDTO) {
        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
        Member member = Member.builder()
                .email(registerDTO.getEmail())
                .password(cryptPasswordEncoder.encode(registerDTO.getPassword()))
                .name(registerDTO.getName())
                .birthYear(registerDTO.getBirthYear())
                .gender(registerDTO.getGender())
                .build();

        try {
            loginRepository.save(member);
        } catch(Exception e){
            log.info(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String getToken(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
        return token;
    }

    @Override
    public String getAuthUser(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("LOGINSERVICE getAuthUser : "+token);
        if(token != null){
            try{
            String user = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX,""))
                    .getBody()
                    .getSubject();
            return user;
        } catch(ExpiredJwtException e){
            log.info("Access Token expired");
            } catch(Exception e){
                log.info("Token parsing error");
            }
        }
        return null;
    }

    @Override
    public String getRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void saveRefreshToken(String email, String refreshToken) {
        long expiration = EXPIRATION_TIME*10;
        tokenService.saveRefreshToken(email, refreshToken, expiration);
    }
}
