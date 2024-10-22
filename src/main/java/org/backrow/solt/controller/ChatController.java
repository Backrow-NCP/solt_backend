package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.backrow.solt.domain.plan.api.ChatResponse;
import org.backrow.solt.service.chatbot.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Tag(name = "챗봇 API", description = "챗봇 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/clovaX")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "챗봇 작성", description = "챗봇에게 메시지 보내는 것을 확인합니다.")
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // 입력 검증
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ChatResponse("메시지를 입력해야 합니다."));
        }

        try {
            // 서비스 호출
            String botReply = chatService.sendMessageToClovaApi(userMessage);

            // 클라이언트로 챗봇 응답 전송
            ChatResponse result = new ChatResponse(botReply);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponse("오류가 발생했습니다."));
        }
    }
}
