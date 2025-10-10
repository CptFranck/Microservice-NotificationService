package com.CptFranck.NotificationService.listener;

import com.CptFranck.dto.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.CptFranck.NotificationService.service.NotificationService;

@Component
@Slf4j
public class BookingEventListener {

    private final NotificationService notificationService;

    public BookingEventListener(NotificationService notificationDispatcherService) {
        this.notificationService = notificationDispatcherService;
    }

    @KafkaListener(topics = "booking-event", groupId = "notification-service")
    public void onBookingEvent(BookingEvent event) {
        log.info("Received booking request: {}", event);
        notificationService.send(event.getUserId().toString(), "/queue/bookings", "booking event", event);
    }
}
