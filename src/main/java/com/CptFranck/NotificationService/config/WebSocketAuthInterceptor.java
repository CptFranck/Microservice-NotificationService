package com.CptFranck.NotificationService.config;

import com.CptFranck.NotificationService.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static org.springframework.messaging.support.MessageHeaderAccessor.getAccessor;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    final private JwtTokenService jwtTokenService;

    public WebSocketAuthInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);
                try {
                    Authentication authentication = jwtTokenService.parseToken(token);
                    accessor.setUser(authentication);
                } catch (Exception e) {
                    log.error("Invalid token", e);
                    accessor.setUser(null);
                    throw new IllegalArgumentException("Invalid or expired token", e);
                }
            } else {
                accessor.setUser(null);
            }
        }
        return message;
    }
}
