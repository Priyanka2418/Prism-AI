package com.aimock.interview.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "transcripts",
        indexes = {
                @Index(name = "idx_transcript_turn_id",
                        columnList = "turn_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "turn_id",
            nullable = false,
            unique = true
    )
    private InterviewTurn turn;

    @Column(
            name = "transcript_text",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String transcriptText;

    @Column(length = 20)
    private String language;

    @Column(length = 100)
    private String provider;

    @Column(
            name = "confidence_score",
            precision = 5,
            scale = 2
    )
    private BigDecimal confidenceScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
