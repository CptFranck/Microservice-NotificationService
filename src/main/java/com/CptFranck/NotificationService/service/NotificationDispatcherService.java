package com.CptFranck.NotificationService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationDispatcherService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationDispatcherService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void send(String destination, String eventType, Object payload) {
        log.info("Sending {} message to {} with : {}", eventType, destination, payload);
        messagingTemplate.convertAndSend(destination, payload);
    }
}
