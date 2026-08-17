package com.aimock.interview.profile.mentor.service;

import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;

import java.util.List;
import java.util.UUID;

public interface MentorProfileService {

    MentorProfileResponse createProfile(
            MentorProfileRequest request);

    MentorProfileResponse getProfileById(UUID id);

    MentorProfileResponse getMyProfile();

    List<MentorProfileResponse> getAllProfiles();

    MentorProfileResponse updateMyProfile(
            MentorProfileRequest request
    );

    void deleteMyProfile();
}