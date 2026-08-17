package com.aimock.interview.profile.candidate.service;

import com.aimock.interview.profile.candidate.dto.CandidateProfileRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;

import java.util.List;
import java.util.UUID;

public interface CandidateProfileService {

    CandidateProfileResponse createProfile( CandidateProfileRequest request);

    CandidateProfileResponse getProfileById(UUID id);

    CandidateProfileResponse getMyProfile();

    List<CandidateProfileResponse> getAllProfiles();

    CandidateProfileResponse updateMyProfile(CandidateProfileRequest request);

    void deleteMyProfile();
}