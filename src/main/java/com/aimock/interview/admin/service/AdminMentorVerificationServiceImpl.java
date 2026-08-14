package com.aimock.interview.admin.service;

import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.enums.VerificationStatus;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminMentorVerificationServiceImpl
        implements AdminMentorVerificationService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;

    @Override
    public List<MentorProfileResponse> getPendingMentors() {

        return mentorProfileRepository
                .findByVerificationStatus(VerificationStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MentorProfileResponse getMentorForVerification(
            UUID mentorProfileId
    ) {

        MentorProfile profile = mentorProfileRepository.findById(mentorProfileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        return mapToResponse(profile);
    }

    @Override
    public List<MentorProfileResponse> getAllMentors() {

        return mentorProfileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MentorProfileResponse verifyMentor(
            UUID mentorProfileId,
            UUID adminUserId
    ) {

        MentorProfile profile = getProfile(mentorProfileId);
        User admin = getAdmin(adminUserId);

        if (profile.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new DuplicateResourceException(
                    "Mentor profile is already " +
                            profile.getVerificationStatus()
            );
        }

        profile.setVerificationStatus(VerificationStatus.VERIFIED);
        profile.setVerifiedBy(admin);
        profile.setVerifiedAt(LocalDateTime.now());
        profile.setRejectionReason(null);

        MentorProfile savedProfile =
                mentorProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    @Override
    public MentorProfileResponse rejectMentor(
            UUID mentorProfileId,
            UUID adminUserId,
            String rejectionReason
    ) {

        MentorProfile profile = getProfile(mentorProfileId);
        User admin = getAdmin(adminUserId);

        if (profile.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new DuplicateResourceException(
                    "Mentor profile is already " +
                            profile.getVerificationStatus()
            );
        }

        profile.setVerificationStatus(VerificationStatus.REJECTED);
        profile.setVerifiedBy(admin);
        profile.setVerifiedAt(LocalDateTime.now());
        profile.setRejectionReason(rejectionReason);

        MentorProfile savedProfile =
                mentorProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    private MentorProfile getProfile(UUID mentorProfileId) {
        return mentorProfileRepository.findById(mentorProfileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));
    }

    private User getAdmin(UUID adminUserId) {

        User user = userRepository.findById(adminUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin user not found"
                        ));

        if (user.getRole() != Role.ADMIN) {
            throw new ForbiddenException(
                    "Only admin users can perform mentor verification"
            );
        }

        return user;
    }

    private MentorProfileResponse mapToResponse(
            MentorProfile profile
    ) {

        return new MentorProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getHeadline(),
                profile.getCompany(),
                profile.getJobTitle(),
                profile.getYearsOfExperience(),
                profile.getExpertise(),
                profile.getBio(),
                profile.getLinkedinUrl(),
                profile.getVerificationStatus(),
                profile.getVerifiedBy() != null
                        ? profile.getVerifiedBy().getId()
                        : null,
                profile.getVerifiedAt(),
                profile.getRejectionReason(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}