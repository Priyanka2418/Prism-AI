package com.aimock.interview.mentoring.chat.config;

import com.aimock.interview.auth.security.ChatSubscriptionInterceptor;
import com.aimock.interview.auth.security.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final ChatSubscriptionInterceptor chatSubscriptionInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        //clients who are subscribed to this destination and should receive messages published there
        registry.enableSimpleBroker("/topic");

        //The client sends a STOMP message to an /app/... destination.
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {

        registration.interceptors(webSocketAuthInterceptor, chatSubscriptionInterceptor);
        //Whenever a STOMP message comes from a client into the server, pass it through WebSocketAuthInterceptor first.
    }
}