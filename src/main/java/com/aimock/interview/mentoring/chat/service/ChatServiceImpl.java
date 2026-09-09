package com.aimock.interview.mentoring.chat.service;

import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.InvalidStateException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.mentoring.chat.dto.ChatMessageResponse;
import com.aimock.interview.mentoring.chat.dto.SendChatMessageRequest;
import com.aimock.interview.mentoring.chat.entity.ChatMessage;
import com.aimock.interview.mentoring.chat.repository.ChatMessageRepository;
import com.aimock.interview.mentoring.session.common.SessionStatus;
import com.aimock.interview.mentoring.session.entity.MentorSession;
import com.aimock.interview.mentoring.session.repository.MentorSessionRepository;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MentorSessionRepository mentorSessionRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<ChatMessageResponse> getChatHistory(UUID sessionId) {

        MentorSession session = getSession(sessionId);

        User currentUser = securityUtils.getCurrentUser();

        validateParticipant(session, currentUser);

        return chatMessageRepository
                .findByMentorSessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(
            UUID sessionId,
            SendChatMessageRequest request,
            Principal principal) {

        MentorSession session = getSession(sessionId);

        if (principal == null) {
            throw new ForbiddenException(
                    "Authentication required"
            );
        }

        User currentUser =
                userRepository
                        .findByEmail(principal.getName())
                        .orElseThrow(() ->
                                new ForbiddenException(
                                        "Authenticated user not found"
                                )
                        );

        validateParticipant(session, currentUser);

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new InvalidStateException(
                    "Chat is only available while the session is in progress"
            );
        }

        ChatMessage message = new ChatMessage();

        message.setMentorSession(session);
        message.setSender(currentUser);
        message.setContent(request.content());

        ChatMessage savedMessage =
                chatMessageRepository.save(message);

        return toResponse(savedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(
            UUID sessionId,
            Long messageId,
            Principal principal) {

        MentorSession session = getSession(sessionId);

        if (principal == null) {
            throw new ForbiddenException("Authentication required");
        }

        User currentUser = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new ForbiddenException("Authenticated user not found"));

        validateParticipant(session, currentUser);

        ChatMessage message = chatMessageRepository
                .findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!message.getMentorSession().getId().equals(sessionId)) {
            throw new ForbiddenException("Message does not belong to this session");
        }

        // Strict ownership check: Only the sender can delete their own message!
        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You cannot delete another user's message");
        }

        chatMessageRepository.delete(message);
    }

    @Override
    @Transactional
    public void clearMyMessages(
            UUID sessionId,
            Principal principal) {

        MentorSession session = getSession(sessionId);

        if (principal == null) {
            throw new ForbiddenException("Authentication required");
        }

        User currentUser = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new ForbiddenException("Authenticated user not found"));

        validateParticipant(session, currentUser);

        chatMessageRepository.deleteByMentorSessionIdAndSenderId(sessionId, currentUser.getId());
    }

    private MentorSession getSession(UUID sessionId) {

        return mentorSessionRepository
                .findByIdForChatAuthorization(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor session not found"));
    }

    private void validateParticipant(
            MentorSession session, User currentUser) {

        UUID candidateUserId = session
                .getMentorRequest()
                .getStudent()
                .getUser()
                .getId();

        UUID mentorUserId = session
                .getMentorRequest()
                .getMentor()
                .getUser()
                .getId();

        UUID currentUserId = currentUser.getId();

        if (!currentUserId.equals(candidateUserId)
                && !currentUserId.equals(mentorUserId)) {

            throw new ForbiddenException(
                    "You are not a participant of this mentoring session");
        }
    }

    private ChatMessageResponse toResponse(ChatMessage message) {

        return new ChatMessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getCreatedAt());
    }
}
