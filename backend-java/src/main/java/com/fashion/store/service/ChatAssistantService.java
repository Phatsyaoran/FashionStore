package com.fashion.store.service;

import com.fashion.store.dto.ChatAssistantRequest;
import com.fashion.store.dto.ChatAssistantResponse;

public interface ChatAssistantService {

    ChatAssistantResponse handleChatMessage(ChatAssistantRequest request);
}
