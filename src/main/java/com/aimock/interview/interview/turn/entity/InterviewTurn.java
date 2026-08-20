package com.aimock.interview.interview.entity;


import com.aimock.interview.interview.enums.AiAction;
import com.aimock.interview.interview.enums.Difficulty;
import com.aimock.interview.interview.enums.Speaker;
import com.aimock.interview.interview.enums.TurnType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "interview_turns",
        indexes = {
                @Index(
                        name = "idx_turn_interview_id", columnList = "interview_id"),
                @Index(
                        name = "idx_turn_parent_id", columnList = "parent_turn_id"),
                @Index(
                        name = "idx_turn_number", columnList = "turn_number")},
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_interview_turn_number",
                        columnNames = {"interview_id", "turn_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class InterviewTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "interview_id",
            nullable = false
    )
    private Interview interview;

    @Column(
            name = "turn_number",
            nullable = false
    )
    private Integer turnNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private Speaker speaker;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "turn_type",
            nullable = false,
            length = 30
    )
    private TurnType turnType;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_turn_id"
    )
    private InterviewTurn parentTurn;

    @Column(length = 150)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "ai_action",
            length = 30
    )
    private AiAction aiAction;

    @Column(name = "answer_duration_seconds")
    private Integer answerDurationSeconds;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
