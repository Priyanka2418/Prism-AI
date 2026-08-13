package com.aimock.interview.profile.candidate.entity;


import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.common.enums.PreferredDomain;
import com.aimock.interview.common.enums.TargetRole;
import com.aimock.interview.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;


@Entity
@Table(
        name = "student_profiles",
        indexes = {
                @Index(name = "idx_student_target_role", columnList = "target_role"),
                @Index(name = "idx_student_domain", columnList = "preferred_domain")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CandidateProfile {

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

    @Column(length = 100)
    private String college;

    @Column(length = 100)
    private String degree;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", length = 50)
    private TargetRole targetRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 30)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_domain", length = 50)
    private PreferredDomain preferredDomain;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> skills;

    @Column(length = 500)
    private String resumeUrl;

}