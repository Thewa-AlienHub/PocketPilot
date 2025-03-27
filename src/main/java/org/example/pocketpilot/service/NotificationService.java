package org.example.pocketpilot.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface NotificationService {


    // Process IMMEDIATE notifications instantly
    @Scheduled(fixedRate = 2000) // Runs every 2 seconds
    void processImmediateNotifications();

    // Process SCHEDULED notifications at 12:00 PM
    @Scheduled(cron = "0 0 12 * * ?") // Runs at exactly 12:00 PM every day
    void processScheduledNotifications();
}
