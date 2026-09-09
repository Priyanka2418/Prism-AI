package com.aimock.interview.mentoring.request.entity;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "mentor_requests",
        indexes = {
                @Index(
                        name = "idx_mentor_request_student",
                        columnList = "student_id"),
                @Index(
                        name = "idx_mentor_request_mentor",
                        columnList = "mentor_id"),
                @Index(
                        name = "idx_mentor_request_interview",
                        columnList = "interview_id"),
                @Index(
                        name = "idx_mentor_request_status",
                        columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MentorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private CandidateProfile student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "mentor_id",
            nullable = false
    )
    private MentorProfile mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Column(name = "requested_start_time",
            nullable = false)
    private LocalDateTime requestedStartTime;

    @Column(name = "requested_duration_minutes",
            nullable = false)
    private Integer requestedDurationMinutes;

    @Column(
            name = "request_message",
            columnDefinition = "TEXT"
    )
    private String requestMessage;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "mentor_accepted_at")
    private LocalDateTime mentorAcceptedAt;

    @Column(name = "mentor_rejection_reason",
            columnDefinition = "TEXT")
    private String mentorRejectionReason;

    @Column(nullable = false, updatable = false)
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