package com.aimock.interview.mentoring.entity;

import com.aimock.interview.interview.entity.Interview;
import com.aimock.interview.user.entity.MentorProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "mentor_interview_views",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_mentor_interview_view",
                        columnNames = {"interview_id", "mentor_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_mentor_interview_view_interview",
                        columnList = "interview_id"
                ),
                @Index(
                        name = "idx_mentor_interview_view_mentor",
                        columnList = "mentor_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MentorInterviewView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "interview_id",
            nullable = false
    )
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "mentor_id",
            nullable = false
    )
    private MentorProfile mentor;

    @Column(
            name = "watch_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal watchPercentage;

    @Column(
            name = "watch_duration_seconds",
            nullable = false
    )
    private Integer watchDurationSeconds;

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