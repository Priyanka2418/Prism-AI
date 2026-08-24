package com.aimock.interview.profile.mentor.service;

import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MentorProfileService {

    MentorProfileResponse createProfile(
            MentorProfileRequest request);

    MentorProfileResponse getProfileById(UUID id);

    MentorProfileResponse getMyProfile();

    List<MentorProfileResponse> getAllProfiles();

    MentorPublicProfileResponse getMyPublicProfile();

    MentorPublicProfileResponse updatePublicProfile(
            MentorPublicProfileUpdateRequest request);

    List<MentorPublicProfileResponse> getPublicMentors();

    void deleteMyProfile();

    MentorPublicProfileResponse getPublicMentorProfile(UUID id);
}