package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.service.mail.MailCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "이메일 인증 API", description = "이메일로 인증번호를 전송하고, 인증번호를 검증하는 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Log4j2
public class MailController {
    private final MailCheckService mailCheckService;

    @Operation(summary="이메일로 인증번호 전송", description ="입력된 이메일 주소로 인증번호를 전송합니다.")
    @PostMapping("/verification-requests")
    public  ResponseEntity<Map<String, Boolean>> sendMessage(@RequestParam("email") String email) {
        log.info("Send verification number : " + email);
        Map<String, Boolean> response = new HashMap<>();
        try {
            mailCheckService.sendCodeToEmail(email);
            response.put("result", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary="인증번호 확인", description ="사용자가 입력한 인증번호가 이메일로 전송된 것과 일치하는지 확인합니다.")
    @GetMapping("/verifications")
    public ResponseEntity<Map<String, Boolean>> verificationEmail(@RequestParam("email") String email,
                                                                  @RequestParam("code") String authCode) {
        Boolean response = mailCheckService.verifiedCode(email, authCode);
        Map<String, Boolean> map = new HashMap<>();
        map.put("result", response);
        return ResponseEntity.ok(map);
    }
}