package com.CptFranck.NotificationService.listener;

import com.CptFranck.dto.BookingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.CptFranck.NotificationService.service.NotificationDispatcherService;

@Component
public class BookingEventListener {

    private final NotificationDispatcherService notificationDispatcherService;

    public BookingEventListener(NotificationDispatcherService notificationDispatcherService) {
        this.notificationDispatcherService = notificationDispatcherService;
    }

    @KafkaListener(topics = "booking-event", groupId = "notification-service")
    public void onBookingEvent(BookingEvent event) {
        notificationDispatcherService.send("/topic/bookings", "booking event", event);
    }
}
