package com.aimock.interview.profile.mentor.entity;

import com.aimock.interview.common.enums.VerificationStatus;
import com.aimock.interview.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "mentor_profiles",
        indexes = {
                @Index(
                        name = "idx_mentor_verification_status",
                        columnList = "verification_status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(length = 200)
    private String headline;

    @Column(length = 150)
    private String company;

    @Column(name = "job_title", length = 150)
    private String jobTitle;

    @Column(name = "years_of_experience", precision = 3, scale = 1)
    private BigDecimal yearsOfExperience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> expertise;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String linkedinUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "verification_status",
            nullable = false,
            length = 20
    )
    private VerificationStatus verificationStatus =
            VerificationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

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