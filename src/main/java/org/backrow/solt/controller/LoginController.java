package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.login.RegisterDTO;
import org.backrow.solt.service.LoginService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary="Login POST + Spring Security", description ="POST 로그인 + Spring Security")
    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody LoginDTO loginDTO) {
        int memberId = loginService.login(loginDTO);
        UsernamePasswordAuthenticationToken creds =
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword());

        Authentication auth = authenticationManager.authenticate(creds);

        String jwts = loginService.getToken(auth.getName());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("memberId", memberId);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(responseBody);
    }

    @Operation(summary="이름 또는 이메일 중복체크 GET", description = "GET으로 중복이름,이메일 확인")
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

    @Operation(summary="입력한 비밀번호가 맞는지 확인 GET", description = "GET으로 이메일과 비밀번호 입력하여 해당 이메일에 비밀번호가 맞는지 boolean으로 체크")
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

    @Operation(summary = "회원가입 POST", description = "POST로 회원가입")
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
