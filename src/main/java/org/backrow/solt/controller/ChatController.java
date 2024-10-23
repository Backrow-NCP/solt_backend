package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.ChatResponse;
import org.backrow.solt.service.chatbot.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "챗봇 API", description = "챗봇과 상호작용하기 위한 API입니다. 사용자가 입력한 메시지를 처리하고 챗봇의 응답을 반환합니다.")
@RestController
@RequestMapping("/chat")
@Log4j2
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "사용자 메시지 전송 및 챗봇 응답 반환", description = "사용자가 입력한 메시지를 챗봇 서비스로 전송하고, 챗봇의 응답을 받아 반환합니다. 입력된 메시지가 유효하지 않거나 오류가 발생하면 적절한 응답 코드와 오류 메시지를 반환합니다.")
    @PostMapping
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody String userMessage) {
        log.info("Received user message: {}", userMessage);
        try {
            ChatResponse chatResponse = chatService.sendMessageToClovaApi(userMessage);
            log.info("Response from ChatService: {}", chatResponse.getContent());
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ChatResponse.builder()
                            .content("지금은 답변을 드릴 수 없어요.")
                            .build());
        }
    }
}
