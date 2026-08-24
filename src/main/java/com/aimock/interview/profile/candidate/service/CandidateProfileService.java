package com.aimock.interview.profile.candidate.service;

import com.aimock.interview.profile.candidate.dto.CandidateProfileCreateRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.dto.CandidateProfileUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface CandidateProfileService {

    CandidateProfileResponse createProfile( CandidateProfileCreateRequest request);

    CandidateProfileResponse getMyProfile();

    CandidateProfileResponse updateMyProfile(CandidateProfileUpdateRequest request);

    void deleteMyProfile();
}