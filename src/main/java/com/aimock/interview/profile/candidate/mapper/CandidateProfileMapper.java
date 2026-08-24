package com.aimock.interview.profile.candidate.mapper;

import com.aimock.interview.profile.candidate.dto.CandidateProfileCreateRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.dto.CandidateProfileUpdateRequest;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CandidateProfileMapper {

    CandidateProfile toEntity(CandidateProfileCreateRequest request);

    CandidateProfileResponse toResponse(CandidateProfile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfile(CandidateProfileUpdateRequest request,
            @MappingTarget CandidateProfile profile);
}