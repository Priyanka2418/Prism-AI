package com.aimock.interview.profile.mentor.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.enums.VerificationStatus;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileUpdateRequest;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.enums.PublicProfileStatus;
import com.aimock.interview.profile.mentor.mapper.MentorProfileMapper;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorProfileServiceImpl implements MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;
    private final SecurityUtils securityUtils;
    private final MentorProfileMapper mentorProfileMapper;

    @Override
    public MentorProfileResponse createProfile(
            MentorProfileRequest request) {

        User user = securityUtils.getCurrentUser();

        if (mentorProfileRepository.findByUserId(user.getId()).isPresent()) {
            throw new DuplicateResourceException(
                    "Mentor profile already exists");
        }

        if (user.getRole() != Role.MENTOR) {
            throw new ForbiddenException(
                    "Only mentor users can create a mentor profile");
        }

        MentorProfile profile =
                mentorProfileMapper.toEntity(request);

        profile.setUser(user);

        MentorProfile savedProfile =
                mentorProfileRepository.save(profile);

        return mentorProfileMapper.toResponse(savedProfile);
    }

    @Override
    public MentorProfileResponse getProfileById(UUID id) {

        MentorProfile profile = mentorProfileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        return mentorProfileMapper.toResponse(profile);
    }

    @Override
    public MentorProfileResponse getMyProfile() {

        User user = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"
                                ));

        return mentorProfileMapper.toResponse(profile);
    }

    @Override
    public List<MentorProfileResponse> getAllProfiles() {

        return mentorProfileRepository.findAll()
                .stream()
                .map(mentorProfileMapper::toResponse)
                .toList();
    }

    @Override
    public MentorPublicProfileResponse getMyPublicProfile() {

        User user = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentor profile not found"));

        return mentorProfileMapper.toPublicResponse(profile);
    }

    @Override
    public MentorPublicProfileResponse updatePublicProfile(
            MentorPublicProfileUpdateRequest request) {

        User user = securityUtils.getCurrentUser();

        MentorProfile profile = mentorProfileRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Mentor Profile not found"));

        mentorProfileMapper.updateProfile(request, profile);

        // If a rejected mentor resubmits their profile, reset status to PENDING for admin review
        if (profile.getVerificationStatus() == VerificationStatus.REJECTED) {
            profile.setVerificationStatus(VerificationStatus.PENDING);
            profile.setRejectionReason(null);
            profile.setVerifiedBy(null);
            profile.setVerifiedAt(null);
        }

        profile.setPublicProfileStatus(
                PublicProfileStatus.COMPLETED);

        MentorProfile savedProfile =
                mentorProfileRepository.save(profile);

        return mentorProfileMapper.toPublicResponse(savedProfile);
    }

    @Override
    public List<MentorPublicProfileResponse> getPublicMentors() {

        List<MentorProfile> profiles =
                mentorProfileRepository
                        .findByVerificationStatusAndPublicProfileStatus(
                                VerificationStatus.VERIFIED,
                                PublicProfileStatus.COMPLETED
                        );


        return profiles.stream()
                .map(mentorProfileMapper::toPublicResponse)
                .toList();
    }

    @Override
    public void deleteMyProfile() {

        User user = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"
                                ));

        mentorProfileRepository.delete(profile);
    }

    @Override
    public MentorPublicProfileResponse getPublicMentorProfile(UUID id) {

        MentorProfile profile =
                mentorProfileRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentor profile not found"));

        if (profile.getVerificationStatus()
                != VerificationStatus.VERIFIED || profile.getPublicProfileStatus()
                != PublicProfileStatus.COMPLETED) {
            throw new ResourceNotFoundException("Mentor profile not found");
        }

        return mentorProfileMapper.toPublicResponse(profile);
    }
}
