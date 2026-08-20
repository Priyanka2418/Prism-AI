package com.aimock.interview.interview.entity;

import com.aimock.interview.interview.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "interview_media",
        indexes = {
                @Index(
                        name = "idx_media_interview_id",
                        columnList = "interview_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class InterviewMedia {

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
            name = "media_type",
            nullable = false,
            length = 10
    )
    private MediaType mediaType;

    @Column(
            name = "storage_provider",
            nullable = false,
            length = 20
    )
    private String storageProvider;

    @Column(
            name = "storage_key",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String storageKey;

    @Column(
            name = "content_type",
            length = 100
    )
    private String contentType;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}