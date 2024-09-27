package org.backrow.solt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.service.MailCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Log4j2
public class MailController {

    private final MailCheckService mailCheckService;

    @PostMapping("/verification-requests")
    public  ResponseEntity<Map<String, Boolean>> sendMessage(@RequestParam("email") @Valid String email) {
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

    @GetMapping("/verifications")
    public ResponseEntity<Map<String, Boolean>> verificationEmail(@RequestParam("email") @Valid String email,
                                                                  @RequestParam("code") String authCode) {
        Boolean response = mailCheckService.verifiedCode(email, authCode);
        Map<String, Boolean> map = new HashMap<>();
        map.put("result", response);
        return ResponseEntity.ok(map);
    }
}