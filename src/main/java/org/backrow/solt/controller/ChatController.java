package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.ChatResponse;
import org.backrow.solt.service.chatbot.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "챗봇 API", description = "챗봇 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/clovaX")
@Log4j2
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "챗봇 작성", description = "챗봇에게 메시지를 보내고 응답을 확인합니다.")
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody String userMessage) {
        log.info("Received user message: {}", userMessage);

        ChatResponse chatResponse = chatService.sendMessageToClovaApi(userMessage);

        // 응답의 content가 null인지 확인하여 오류 처리
        if (chatResponse.getContent() == null) {
            log.error("Response content is null, returning bad request.");
            return ResponseEntity.badRequest().body(chatResponse); // 오류 메시지와 함께 400 반환
        }
        log.info("Response from ChatService: {}", chatResponse.getContent());
        return ResponseEntity.ok(chatResponse);
    }
}
