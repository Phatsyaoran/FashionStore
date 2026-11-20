package com.fashion.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAssistantRequest {

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String message;

    private String sessionId;
}
