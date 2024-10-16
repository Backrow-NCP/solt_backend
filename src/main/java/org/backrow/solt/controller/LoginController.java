package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.login.RegisterDTO;
import org.backrow.solt.security.CustomUserDetails;
import org.backrow.solt.service.security.LoginService;
import org.backrow.solt.service.security.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "회원 관리 API", description = "로그인 및 회원가입, 회원확인, 중복 체크, 비밀번호 확인 등의 기능을 수행하는 API입니다.")
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Operation(summary="로그인", description ="회원 이메일과 비밀번호로 로그인하여 Access 토큰과 Refresh 토큰을 발급합니다.")
    @PostMapping
    public ResponseEntity<?> getToken(@RequestBody LoginDTO loginDTO) {
        try {
            long memberId = loginService.login(loginDTO);
            UsernamePasswordAuthenticationToken creds =
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword());

            Authentication auth = authenticationManager.authenticate(creds);

            String jwts = loginService.getToken(auth.getName(), ((CustomUserDetails) auth.getPrincipal()).getMemberId());
            String refreshToken = loginService.getRefreshToken();
            loginService.saveRefreshToken(auth.getName(), refreshToken);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("memberId", memberId);
            responseBody.put("refreshToken", refreshToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                    .body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Access 토큰 갱신", description = "Refresh 토큰을 사용하여 새로운 Access 토큰을 발급합니다.")
    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(@RequestBody String email, String refreshToken) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            tokenService.validateRefreshToken(email, refreshToken);

            CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

            String newAccessToken = loginService.getToken(email, user.getMemberId());

            responseBody.put("result","TRUE");

            Authentication auth =  new UsernamePasswordAuthenticationToken(email,
                    null, java.util.Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                    .body(responseBody);

        } catch (Exception e) {
            responseBody.put("result","RefreshToken_Expired");
            return ResponseEntity.status(401).body(responseBody);
        }
    }

    @Operation(summary="중복 체크", description = "이름 또는 이메일이 이미 사용 중인지 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<Map<String,Boolean>> checkExist(String value, String type) {
        try {
            Map<String,Boolean> response = new HashMap<>();
            response.put("result", loginService.checkExist(value, type));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary="비밀번호 확인", description = "입력한 이메일과 비밀번호가 일치하는지 확인합니다.")
    @GetMapping("/password")
    public ResponseEntity<Map<String,Boolean>> checkPassword(String email, String password){
        try {
            Map<String,Boolean> response = new HashMap<>();
            response.put("result", loginService.checkPassword(email, password));
            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "회원가입", description = "회원 정보를 입력하여 새로 회원가입을 합니다.")
    @PostMapping("/register")
    public ResponseEntity<Map<String,Boolean>> register(RegisterDTO registerDTO){
        String regex = "^(?=.*[!@#$%^&*()_+=-]).{8,20}$";
        if(registerDTO.getPassword().matches(regex)) {
           try {
               Map<String, Boolean> response = new HashMap<>();
               response.put("result", loginService.register(registerDTO));
               return ResponseEntity.ok(response);
           } catch (Exception e) {
               log.error(e.getMessage());
               return ResponseEntity.internalServerError().build();
           }
       }
       return ResponseEntity.badRequest().build();
    }
}
