package com.aimock.interview.profile.candidate.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.candidate.dto.CandidateProfileCreateRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.dto.CandidateProfileUpdateRequest;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.profile.candidate.mapper.CandidateProfileMapper;
import com.aimock.interview.profile.candidate.repository.CandidateProfileRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final SecurityUtils securityUtils;
    private final CandidateProfileMapper candidateProfileMapper;


    @Override
    public CandidateProfileResponse createProfile(
            CandidateProfileCreateRequest request) {

        User user = securityUtils.getCurrentUser();

        UUID userId = user.getId();

        if (candidateProfileRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException(
                    "Candidate profile already exists");
        }

        CandidateProfile profile =
                candidateProfileMapper.toEntity(request);

        profile.setUser(user);

        CandidateProfile savedProfile =
                candidateProfileRepository.save(profile);

        return candidateProfileMapper.toResponse(savedProfile);
    }

    @Override
    public CandidateProfileResponse getMyProfile() {

        CandidateProfile profile = candidateProfileRepository
                .findByUserId(securityUtils.getCurrentUser().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate profile not found"));

        return candidateProfileMapper.toResponse(profile);
    }

    @Override
    public CandidateProfileResponse updateMyProfile(
            CandidateProfileUpdateRequest request) {

        UUID userId = securityUtils.getCurrentUser().getId();

        CandidateProfile profile = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate profile not found"));

        candidateProfileMapper.updateProfile(request, profile);

        CandidateProfile updatedProfile =
                candidateProfileRepository.save(profile);

        return candidateProfileMapper.toResponse(updatedProfile);
    }

    @Override
    public void deleteMyProfile() {

        UUID userId = securityUtils.getCurrentUser().getId();

        CandidateProfile profile = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Candidate profile not found"));

        candidateProfileRepository.delete(profile);
    }
}