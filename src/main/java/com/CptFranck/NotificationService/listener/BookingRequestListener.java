package com.CptFranck.NotificationService.listener;

import com.CptFranck.dto.BookingConfirmed;
import com.CptFranck.dto.BookingEvent;
import com.CptFranck.dto.BookingRejected;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.CptFranck.NotificationService.service.NotificationService;

@Component
@Slf4j
public class BookingRequestListener {

    private final NotificationService notificationService;

    public BookingRequestListener(NotificationService notificationDispatcherService) {
        this.notificationService = notificationDispatcherService;
    }

    @KafkaListener(topics = "booking-event", groupId = "notification-service")
    public void onBookingEvent(BookingEvent event) {
        log.info("Received booking event: {}", event);
        notificationService.sendToUserBooking(event.getUserId().toString(), event);
    }

    @KafkaListener(topics = "booking-confirmed", groupId = "notification-service")
    public void onBookingConfirmed(BookingConfirmed event) {
        log.info("Received booking confirmed: {}", event);
        notificationService.sendToUserBooking(event.getUserId().toString(), event);
    }

    @KafkaListener(topics = "booking-rejected", groupId = "notification-service")
    public void onBookingRejected(BookingRejected event) {
        log.info("Received booking rejected: {}", event);
        notificationService.sendToUserBooking(event.getUserId().toString(), event);
    }
}
