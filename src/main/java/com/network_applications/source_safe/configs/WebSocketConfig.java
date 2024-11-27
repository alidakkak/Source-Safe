package com.network_applications.source_safe.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure the message broker to handle topics and queues
        config.enableSimpleBroker("/topic"); // الإشعارات تُرسل عبر /topic
        config.setApplicationDestinationPrefixes("/app"); // الطلبات تُرسل عبر /app
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // نقطة الاتصال لـ WebSocket
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
