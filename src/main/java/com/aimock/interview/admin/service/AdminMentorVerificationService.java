package com.aimock.interview.admin.service;

import com.aimock.interview.admin.dto.MentorVerificationResponse;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;

import java.util.List;
import java.util.UUID;

public interface AdminMentorVerificationService {

    List<MentorProfileResponse> getPendingMentors();

    MentorProfileResponse getMentorForVerification(UUID mentorProfileId);

    MentorVerificationResponse verifyMentor(UUID mentorProfileId);

    MentorVerificationResponse  rejectMentor(
            UUID mentorProfileId,
            String rejectionReason
    );

    List<MentorProfileResponse> getAllMentors();
}