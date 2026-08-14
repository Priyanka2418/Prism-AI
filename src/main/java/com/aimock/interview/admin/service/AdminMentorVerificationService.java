package com.aimock.interview.admin.service;

import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;

import java.util.List;
import java.util.UUID;

public interface AdminMentorVerificationService {

    List<MentorProfileResponse> getPendingMentors();

    MentorProfileResponse getMentorForVerification(UUID mentorProfileId);

    MentorProfileResponse verifyMentor(UUID mentorProfileId, UUID adminUserId);

    MentorProfileResponse rejectMentor(
            UUID mentorProfileId,
            UUID adminUserId,
            String rejectionReason
    );

    List<MentorProfileResponse> getAllMentors();
}