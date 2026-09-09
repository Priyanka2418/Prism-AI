package com.aimock.interview.mentoring.chat.controller;

import com.aimock.interview.mentoring.chat.dto.ChatMessageResponse;
import com.aimock.interview.mentoring.chat.dto.SendChatMessageRequest;
import com.aimock.interview.mentoring.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/mentor-sessions/{sessionId}/chat")
    @SendTo("/topic/mentor-sessions/{sessionId}/chat")
    public ChatMessageResponse sendMessage(
            @DestinationVariable UUID sessionId,
            SendChatMessageRequest request,
            Principal principal) {

        return chatService.sendMessage(
                sessionId,
                request, principal);
    }
}