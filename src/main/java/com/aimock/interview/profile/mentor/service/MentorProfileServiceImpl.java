package com.aimock.interview.profile.mentor.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
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

    @Override
    public MentorProfileResponse createProfile(
            MentorProfileRequest request
    ) {

        User user = securityUtils.getCurrentUser();

        if (mentorProfileRepository.findByUserId(user.getId()).isPresent()) {
            throw new DuplicateResourceException(
                    "Mentor profile already exists"
            );
        }

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
    public MentorProfileResponse getMyProfile() {

        User user = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"
                                ));

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
    public MentorProfileResponse updateMyProfile(
            MentorProfileRequest request
    ) {

        User user = securityUtils.getCurrentUser();

        MentorProfile profile =
                mentorProfileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"
                                ));

        profile.setHeadline(request.getHeadline());
        profile.setCompany(request.getCompany());
        profile.setJobTitle(request.getJobTitle());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setExpertise(request.getExpertise());
        profile.setBio(request.getBio());
        profile.setLinkedinUrl(request.getLinkedinUrl());

        return mapToResponse(
                mentorProfileRepository.save(profile)
        );
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
