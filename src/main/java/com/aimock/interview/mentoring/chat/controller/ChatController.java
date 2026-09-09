package com.aimock.interview.mentoring.chat.controller;

import com.aimock.interview.mentoring.chat.dto.ChatMessageResponse;
import com.aimock.interview.mentoring.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mentor-sessions/{sessionId}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR')")
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(
            @PathVariable UUID sessionId) {

        return ResponseEntity.ok(
                chatService.getChatHistory(sessionId));
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR')")
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable UUID sessionId,
            @jakarta.validation.Valid @RequestBody com.aimock.interview.mentoring.chat.dto.SendChatMessageRequest request,
            Principal principal) {

        return ResponseEntity.ok(
                chatService.sendMessage(sessionId, request, principal));
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR')")
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID sessionId,
            @PathVariable Long messageId,
            Principal principal) {

        chatService.deleteMessage(sessionId, messageId, principal);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR')")
    @DeleteMapping("/messages/my")
    public ResponseEntity<Void> clearMyMessages(
            @PathVariable UUID sessionId,
            Principal principal) {

        chatService.clearMyMessages(sessionId, principal);
        return ResponseEntity.noContent().build();
    }
}