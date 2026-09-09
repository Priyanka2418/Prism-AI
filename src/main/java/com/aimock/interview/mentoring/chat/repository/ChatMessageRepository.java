package com.aimock.interview.mentoring.chat.repository;

import com.aimock.interview.mentoring.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage , Long> {
    List<ChatMessage> findByMentorSessionIdOrderByCreatedAtAsc(
            UUID mentorSessionId
    );

    void deleteByMentorSessionIdAndSenderId(UUID mentorSessionId, UUID senderId);

    void deleteByMentorSessionId(UUID mentorSessionId);
}
