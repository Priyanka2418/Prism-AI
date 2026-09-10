package com.aimock.interview.mentoring.session.entity;

import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.session.common.SessionStateTransition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.aimock.interview.mentoring.session.common.SessionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "mentor_sessions",
        indexes = {
                @Index(
                        name = "idx_mentor_session_request",
                        columnList = "mentor_request_id"
                ),
                @Index(
                        name = "idx_mentor_session_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_mentor_session_start_at",
                        columnList = "scheduled_start_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MentorSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "mentor_request_id",
            nullable = false,
            unique = true
    )
    private MentorRequest mentorRequest;

    @Column(
            name = "scheduled_start_at",
            nullable = false
    )
    private LocalDateTime scheduledStartAt;

    @Column(
            name = "scheduled_end_at",
            nullable = false
    )
    private LocalDateTime scheduledEndAt;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private SessionStatus status = SessionStatus.UPCOMING;

    @Column(name = "deleted_by_student", nullable = false)
    private boolean deletedByStudent = false;

    @Column(name = "deleted_by_mentor", nullable = false)
    private boolean deletedByMentor = false;

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

    public void transitionTo(SessionStatus targetStatus) {
        if (!SessionStateTransition.isAllowed(this.status, targetStatus)) {
            throw new IllegalStateException(
                    "Invalid session status transition: " + this.status + " -> " + targetStatus);
        }
        this.status = targetStatus;
    }
}