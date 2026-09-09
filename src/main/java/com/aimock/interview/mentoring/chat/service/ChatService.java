package com.aimock.interview.mentoring.chat.service;

import com.aimock.interview.mentoring.chat.dto.ChatMessageResponse;
import com.aimock.interview.mentoring.chat.dto.SendChatMessageRequest;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ChatMessageResponse> getChatHistory(UUID sessionId);

    ChatMessageResponse sendMessage(
            UUID sessionId,
            SendChatMessageRequest request,
            Principal principal);

    void deleteMessage(
            UUID sessionId,
            Long messageId,
            Principal principal);

    void clearMyMessages(
            UUID sessionId,
            Principal principal);
}