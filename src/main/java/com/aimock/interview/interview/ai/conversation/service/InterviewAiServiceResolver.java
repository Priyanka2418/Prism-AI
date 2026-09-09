package com.aimock.interview.interview.ai.conversation.service;

import com.aimock.interview.interview.commons.enums.InterviewType;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
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

        if (interviewType == null) {
            return services.getOrDefault(InterviewType.TECHNICAL, services.values().iterator().next());
        }

        InterviewAiService service = services.get(interviewType);

        if (service != null) {
            return service;
        }

        if (services.isEmpty()) {
            throw new IllegalStateException("No InterviewAiService instances registered");
        }

        // Final fallback — should never reach here if all types have dedicated services
        return services.getOrDefault(InterviewType.TECHNICAL, services.values().iterator().next());
    }
}
