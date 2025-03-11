package org.example.pocketpilot.components;

import org.example.pocketpilot.enums.NotificationType;
import org.example.pocketpilot.model.NotificationModel;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class NotificationQueue {

    private final BlockingQueue<NotificationModel> immediateQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<NotificationModel> scheduledQueue = new LinkedBlockingQueue<>();

    public void enqueue(NotificationModel notification) {
        if (notification.getType() == NotificationType.IMMEDIATE) {
            immediateQueue.add(notification);
        } else {
            scheduledQueue.add(notification);
        }
    }

    public NotificationModel dequeueImmediate() throws InterruptedException {
        return immediateQueue.take(); // Blocks if empty
    }

    public NotificationModel dequeueScheduled() throws InterruptedException {
        return scheduledQueue.take(); // Blocks if empty
    }

    public boolean hasScheduledNotifications() {
        return !scheduledQueue.isEmpty();
    }

}
