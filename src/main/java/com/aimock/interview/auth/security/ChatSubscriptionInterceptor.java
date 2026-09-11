package com.aimock.interview.auth.security;

import com.aimock.interview.mentoring.session.common.SessionStatus;
import com.aimock.interview.mentoring.session.entity.MentorSession;
import com.aimock.interview.mentoring.session.repository.MentorSessionRepository;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatSubscriptionInterceptor
        implements ChannelInterceptor {

    private final MentorSessionRepository mentorSessionRepository;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            System.out.println("========== SUBSCRIBE START ==========");

            String destination = accessor.getDestination();

            System.out.println("Destination = " + destination);

            if (destination == null
                    || !destination.startsWith("/topic/mentor-sessions/")
                    || !destination.endsWith("/chat")) {

                throw new MessageDeliveryException(
                        "Invalid chat destination"
                );
            }

            String[] parts = destination.split("/");

            System.out.println("Destination parts = " + parts.length);

            if (parts.length != 5) {
                throw new MessageDeliveryException(
                        "Invalid chat destination"
                );
            }

            String sessionIdString = parts[3];

            System.out.println(
                    "Session ID string = " + sessionIdString
            );

            UUID sessionId = UUID.fromString(sessionIdString);

            System.out.println(
                    "Session UUID = " + sessionId
            );

            MentorSession mentorSession =
                    mentorSessionRepository
                            .findByIdForChatAuthorization(sessionId)
                            .orElseThrow(() ->
                                    new MessageDeliveryException(
                                            "Mentor session not found"
                                    )
                            );
            if (mentorSession.getStatus() != SessionStatus.IN_PROGRESS) {
                throw new MessageDeliveryException(
                        "Chat is only available while the session is in progress"
                );
            }

            System.out.println(
                    "MentorSession loaded = " + mentorSession.getId()
            );

            System.out.println(
                    "Session status = " + mentorSession.getStatus()
            );

            Principal principal = accessor.getUser();

            System.out.println(
                    "Principal = " + principal
            );

            if (principal == null) {
                throw new MessageDeliveryException(
                        "Authentication required"
                );
            }

            String username = principal.getName();

            System.out.println(
                    "Username = " + username
            );

            User currentUser =
                    userRepository
                            .findByEmail(username)
                            .orElseThrow(() ->
                                    new MessageDeliveryException(
                                            "Authenticated user not found"
                                    )
                            );

            System.out.println(
                    "Current user loaded = " + currentUser.getId()
            );

            System.out.println("Getting mentor request...");

            var mentorRequest =
                    mentorSession.getMentorRequest();

            System.out.println(
                    "Mentor request = " + mentorRequest.getId()
            );

            System.out.println("Getting student...");

            var student =
                    mentorRequest.getStudent();

            System.out.println(
                    "Student = " + student.getId()
            );

            System.out.println("Getting candidate user...");

            UUID candidateUserId =
                    student
                            .getUser()
                            .getId();

            System.out.println(
                    "Candidate user ID = " + candidateUserId
            );

            System.out.println("Getting mentor...");

            var mentor =
                    mentorRequest.getMentor();

            System.out.println(
                    "Mentor = " + mentor.getId()
            );

            System.out.println("Getting mentor user...");

            UUID mentorUserId =
                    mentor
                            .getUser()
                            .getId();

            System.out.println(
                    "Mentor user ID = " + mentorUserId
            );

            UUID currentUserId = currentUser.getId();

            System.out.println(
                    "Current user ID = " + currentUserId
            );

            if (!currentUserId.equals(candidateUserId)
                    && !currentUserId.equals(mentorUserId)) {

                throw new MessageDeliveryException(
                        "You are not a participant of this mentoring session"
                );
            }

            System.out.println(
                    "SUBSCRIBE AUTHORIZATION SUCCESS"
            );

            System.out.println("========== SUBSCRIBE END ==========");
        }

        return message;
    }
}