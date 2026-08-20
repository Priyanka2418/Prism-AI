package com.aimock.interview.interview.ai.conversation.service;

import com.aimock.interview.interview.commons.enums.InterviewType;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class InterviewAiServiceResolver {

    private final Map<InterviewType, InterviewAiService> services;

    public InterviewAiServiceResolver(
            List<InterviewAiService> services) {

        this.services = new EnumMap<>(InterviewType.class);

        for (InterviewAiService service : services) {
            this.services.put(
                    service.getInterviewType(),
                    service);
        }
    }

    public InterviewAiService resolve(
            InterviewType interviewType) {

        InterviewAiService service = services.get(interviewType);

        if (service == null) {
            throw new IllegalArgumentException(
                    "No AI service configured for interview type: "
                            + interviewType);
        }

        return service;
    }
}
