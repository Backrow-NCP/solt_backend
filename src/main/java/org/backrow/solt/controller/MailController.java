package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.service.MailCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "이메일인증 API", description = "이메일 인증번호 보내기, 인증번호 확인 ")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Log4j2
public class MailController {

    private final MailCheckService mailCheckService;

    @Operation(summary="mail인증번호 보내기", description ="POST 요청으로 메일 인증번호 보내기")
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

    @Operation(summary="인증번호로 인증요청", description ="GET으로 인증요청")
    @GetMapping("/verifications")
    public ResponseEntity<Map<String, Boolean>> verificationEmail(@RequestParam("email") String email,
                                                                  @RequestParam("code") String authCode) {
        Boolean response = mailCheckService.verifiedCode(email, authCode);
        Map<String, Boolean> map = new HashMap<>();
        map.put("result", response);
        return ResponseEntity.ok(map);
    }
}