package com.aimock.interview.mentoring.chat.entity;

import com.aimock.interview.mentoring.session.entity.MentorSession;
import com.aimock.interview.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(
                        name = "idx_chat_message_sender",
                        columnList = "sender_id"
                ),
                @Index(
                        name = "idx_chat_message_session_created_at",
                        columnList = "mentor_session_id, created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "mentor_session_id",
            nullable = false
    )
    private MentorSession mentorSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "sender_id",
            nullable = false
    )
    private User sender;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;

    }
}
