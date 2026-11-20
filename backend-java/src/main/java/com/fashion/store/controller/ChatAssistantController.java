package com.fashion.store.controller;

import com.fashion.store.dto.ApiResponse;
import com.fashion.store.dto.ChatAssistantRequest;
import com.fashion.store.dto.ChatAssistantResponse;
import com.fashion.store.service.ChatAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-assistant")
@RequiredArgsConstructor
@Tag(name = "Chat Assistant API", description = "Trợ lý ảo AI tư vấn chọn size, tìm sản phẩm và tra cứu đơn hàng")
public class ChatAssistantController {

    private final ChatAssistantService chatAssistantService;

    @PostMapping
    @Operation(summary = "Gửi câu hỏi tư vấn đến Trợ lý AI và nhận gợi ý thông minh")
    public ResponseEntity<ApiResponse<ChatAssistantResponse>> chat(@Valid @RequestBody ChatAssistantRequest request) {
        ChatAssistantResponse response = chatAssistantService.handleChatMessage(request);
        return ResponseEntity.ok(ApiResponse.ok("Phản hồi từ trợ lý AI", response));
    }
}
