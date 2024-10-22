package org.backrow.solt.controller;

import org.backrow.solt.service.chatbot.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clovaX")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        try {
            // 서비스 호출
            String botReply = chatService.sendMessageToClovaApi(userMessage);

            // 클라이언트로 챗봇 응답 전송
            Map<String, String> result = new HashMap<>();
            result.put("reply", botReply);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 오류 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("reply", "오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
