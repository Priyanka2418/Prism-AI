package com.aimock.interview.profile.mentor.mapper;

import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileUpdateRequest;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MentorProfileMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "verifiedBy", source = "verifiedBy.id")
    MentorProfileResponse toResponse(MentorProfile profile);

    MentorPublicProfileResponse toPublicResponse(MentorProfile profile);

    MentorProfile toEntity(MentorProfileRequest request);

    void updateProfile(
            MentorPublicProfileUpdateRequest request,
            @MappingTarget MentorProfile profile);

}