package com.aimock.interview.interview.ai.evaluation.entity;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.ai.evaluation.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "interview_feedback",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_interview_feedback_interview_id",
                        columnNames = "interview_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_interview_feedback_interview_id",
                        columnList = "interview_id"
                ),
                @Index(
                        name = "idx_interview_feedback_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "interview_id",
            nullable = false,
            unique = true
    )
    private Interview interview;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private FeedbackStatus status;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(
            name = "overall_reason",
            columnDefinition = "TEXT"
    )
    private String overallReason;

    @Column(name = "answer_quality_rating")
    private Integer answerQualityRating;

    @Column(
            name = "answer_quality_reason",
            columnDefinition = "TEXT"
    )
    private String answerQualityReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> keyStrengths;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> developmentAreas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> recommendedPractice;


//    private List<Object> deepDive;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}