package com.aimock.interview.profile.mentor.service;

import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;

import java.util.List;
import java.util.UUID;

public interface MentorProfileService {

    MentorProfileResponse createProfile(
            UUID userId,
            MentorProfileRequest request
    );

    MentorProfileResponse getProfileById(UUID id);

    MentorProfileResponse getProfileByUserId(UUID userId);

    List<MentorProfileResponse> getAllProfiles();

    MentorProfileResponse updateProfile(
            UUID userId,
            MentorProfileRequest request
    );

    void deleteProfile(UUID userId);
}