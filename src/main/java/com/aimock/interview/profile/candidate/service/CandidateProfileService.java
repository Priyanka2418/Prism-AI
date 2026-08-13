package com.aimock.interview.profile.candidate.service;

import com.aimock.interview.profile.candidate.dto.CandidateProfileRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;

import java.util.List;
import java.util.UUID;

public interface CandidateProfileService {

    CandidateProfileResponse createProfile(UUID userId, CandidateProfileRequest request);

    CandidateProfileResponse getProfileById(UUID id);

    CandidateProfileResponse getProfileByUserId(UUID userId);

    List<CandidateProfileResponse> getAllProfiles();

    CandidateProfileResponse updateProfile(UUID id, CandidateProfileRequest request);

    void deleteProfile(UUID id);
}