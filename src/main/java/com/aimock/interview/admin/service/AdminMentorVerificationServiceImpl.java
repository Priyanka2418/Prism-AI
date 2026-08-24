package com.aimock.interview.admin.service;

import com.aimock.interview.admin.dto.MentorVerificationResponse;
import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.enums.VerificationStatus;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.mapper.MentorProfileMapper;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
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
    private final SecurityUtils securityUtils;
    private final MentorProfileMapper mentorProfileMapper;

    @Override
    public List<MentorProfileResponse> getPendingMentors() {

        return mentorProfileRepository
                .findByVerificationStatus(VerificationStatus.PENDING)
                .stream()
                .map(mentorProfileMapper::toResponse)
                .toList();
    }

    @Override
    public MentorProfileResponse getMentorForVerification(
            UUID mentorProfileId
    ) {

        MentorProfile profile = mentorProfileRepository.findById(mentorProfileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        return mentorProfileMapper.toResponse(profile);
    }

    @Override
    public List<MentorProfileResponse> getAllMentors() {

        return mentorProfileRepository.findAll()
                .stream()
                .map(mentorProfileMapper::toResponse)
                .toList();
    }

    @Override
    public MentorVerificationResponse verifyMentor(UUID mentorProfileId) {

        User admin = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findById(mentorProfileId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentor profile not found"));

        if (profile.getVerificationStatus()
                != VerificationStatus.PENDING) {
            throw new IllegalStateException(
                    "Mentor profile is not pending verification");
        }

        profile.setVerificationStatus(VerificationStatus.VERIFIED);

        profile.setVerifiedBy(admin);
        profile.setVerifiedAt(LocalDateTime.now());
        profile.setRejectionReason(null);

        MentorProfile savedProfile = mentorProfileRepository.save(profile);

        return new MentorVerificationResponse(
                savedProfile.getId(),
                savedProfile.getVerificationStatus());
    }

    @Override
    public MentorVerificationResponse rejectMentor(
            UUID mentorProfileId, String rejectionReason) {

        User admin = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findById(mentorProfileId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"));

        if (profile.getVerificationStatus()
                != VerificationStatus.PENDING) {
            throw new IllegalStateException(
                    "Mentor profile is not pending verification");
        }

        profile.setVerificationStatus(VerificationStatus.REJECTED);

        profile.setVerifiedBy(admin);
        profile.setVerifiedAt(LocalDateTime.now());
        profile.setRejectionReason(rejectionReason);

        MentorProfile savedProfile = mentorProfileRepository.save(profile);

        return new MentorVerificationResponse(
                savedProfile.getId(),
                savedProfile.getVerificationStatus());
    }
}