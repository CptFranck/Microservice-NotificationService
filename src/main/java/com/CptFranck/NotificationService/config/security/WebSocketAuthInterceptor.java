package com.CptFranck.NotificationService.config.security;

import com.CptFranck.NotificationService.service.JwtTokenService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

import static org.springframework.messaging.support.MessageHeaderAccessor.getAccessor;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenService jwtTokenService;

    public WebSocketAuthInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = getAccessor(message, StompHeaderAccessor.class);

        if(accessor == null)
            throw new MessagingException("No StompHeaderAccessor found");

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateStompUser(accessor);

        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            checkUserSubscription(accessor);
        }
        return message;
    }

    private void authenticateStompUser(StompHeaderAccessor accessor){
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            try {
                Authentication authentication = jwtTokenService.parseToken(token);
                accessor.setUser(authentication);
            } catch (Exception e) {
                accessor.setUser(null);
                log.error("Invalid token", e);
                throw new IllegalArgumentException("Invalid or expired token", e);
            }
        } else {
            accessor.setUser(null);
        }
    }

    private void checkUserSubscription(StompHeaderAccessor accessor){
        Principal user = accessor.getUser();
        String dest = accessor.getDestination();

        if(dest == null || user == null)
            throw new MessagingException("No Destination found or User not found");

        if (dest.startsWith("/user/") && !dest.contains(user.getName())) {
            log.error("Wrong user trying to subscribe");
            try {
                throw new IllegalAccessException("Access denied");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
