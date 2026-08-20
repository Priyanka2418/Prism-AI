package com.aimock.interview.interview.entity;

import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.interview.enums.Difficulty;
import com.aimock.interview.interview.enums.InterviewStatus;
import com.aimock.interview.interview.enums.InterviewType;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
        name = "interviews",
        indexes = {
                @Index(
                        name = "idx_interview_student_id",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_interview_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private CandidateProfile student;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "interview_type",
            nullable = false,
            length = 30
    )
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private Difficulty interviewDifficulty;

    @Column(
            name = "target_role",
            nullable = false,
            length = 150
    )
    private String targetRole;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "experience_level",
            nullable = false,
            length = 30
    )
    private ExperienceLevel experienceLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> topics;

    @Column(
            name = "duration_minutes",
            nullable = false
    )
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private InterviewStatus status = InterviewStatus.CREATED;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

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