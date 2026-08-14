package com.aimock.interview.profile.mentor.service;

import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorProfileServiceImpl implements MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;

    @Override
    public MentorProfileResponse createProfile(
            UUID userId,
            MentorProfileRequest request
    ) {

        if (mentorProfileRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("Mentor profile already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.MENTOR) {
            throw new ForbiddenException(
                    "Only mentor users can create a mentor profile"
            );
        }

        MentorProfile profile = new MentorProfile();

        profile.setUser(user);
        profile.setHeadline(request.getHeadline());
        profile.setCompany(request.getCompany());
        profile.setJobTitle(request.getJobTitle());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setExpertise(request.getExpertise());
        profile.setBio(request.getBio());
        profile.setLinkedinUrl(request.getLinkedinUrl());

        MentorProfile savedProfile =
                mentorProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    @Override
    public MentorProfileResponse getProfileById(UUID id) {

        MentorProfile profile = mentorProfileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        return mapToResponse(profile);
    }

    @Override
    public MentorProfileResponse getProfileByUserId(UUID userId) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        return mapToResponse(profile);
    }

    @Override
    public List<MentorProfileResponse> getAllProfiles() {

        return mentorProfileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MentorProfileResponse updateProfile(
            UUID userId,
            MentorProfileRequest request
    ) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        profile.setHeadline(request.getHeadline());
        profile.setCompany(request.getCompany());
        profile.setJobTitle(request.getJobTitle());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setExpertise(request.getExpertise());
        profile.setBio(request.getBio());
        profile.setLinkedinUrl(request.getLinkedinUrl());

        MentorProfile updatedProfile =
                mentorProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    @Override
    public void deleteProfile(UUID userId) {

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found"));

        mentorProfileRepository.delete(profile);
    }

    private MentorProfileResponse mapToResponse(MentorProfile profile) {

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
