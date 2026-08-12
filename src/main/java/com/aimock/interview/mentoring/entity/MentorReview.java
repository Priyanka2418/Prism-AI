package com.aimock.interview.mentoring.entity;

import com.aimock.interview.interview.entity.Interview;
import com.aimock.interview.user.entity.MentorProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "mentor_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_mentor_review_interview_mentor",
                        columnNames = {
                                "interview_id",
                                "mentor_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MentorReview {

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
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String strengths;

    @Column(
            name = "areas_for_improvement",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String areasForImprovement;

    @Column(
            name = "overall_feedback",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String overallFeedback;

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
