package org.example.pocketpilot.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.components.NotificationQueue;
import org.example.pocketpilot.entities.BudgetEntity;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.NotificationModel;
import org.example.pocketpilot.repository.BudgetRepository;
import org.example.pocketpilot.service.BudgetService;
import org.example.pocketpilot.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationQueue notificationQueue;
    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    public NotificationServiceImpl(NotificationQueue notificationQueue, BudgetService budgetService, BudgetRepository budgetRepository) {
        this.notificationQueue = notificationQueue;
        this.budgetService = budgetService;
        this.budgetRepository = budgetRepository;
    }

    // Process IMMEDIATE notifications instantly
    @Override
    @Scheduled(fixedRate = 2000) // Runs every 2 seconds
    public void processImmediateNotifications() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                NotificationModel notification = notificationQueue.dequeueImmediate();
                sendNotification(notification);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Process SCHEDULED notifications at 12:00 PM
    @Override
    @Scheduled(cron = "0 0 12 * * ?") // Runs at exactly 12:00 PM every day
    public void processScheduledNotifications() {
        while (notificationQueue.hasScheduledNotifications()) {
            try {
                NotificationModel notification = notificationQueue.dequeueScheduled();
                sendNotification(notification);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

//    @Scheduled(cron = "0 0 0 L * ?") // Runs at midnight on the last day of each month
//    public void sendMonthlyBudgetNotifications() {
//        log.info("🔔 Starting Monthly Budget Notifications...");
//
//        List<BudgetEntity> budgets = budgetRepository.findAllBudgetsForCurrentMonth();
//
//        for (BudgetEntity budget : budgets) {
//            BigDecimal spent = budget.getSpentAmount();
//            BigDecimal budgetAmount = budget.getBudgetAmount();
//
//            // Calculate recommended budget (based on last max 5 months)
//            BigDecimal recommendedBudget = budgetService.calculateRecommendedBudget(budget.getUserId(), budget.getCategory(),budget.getYearMonth());
//
//            String message;
//            if (spent.compareTo(budgetAmount) > 0) {
//                message = "🚨 Budget Alert: You exceeded your budget for " + budget.getCategory() +
//                        " by " + spent.subtract(budgetAmount) + ". Consider adjusting next month's budget.";
//            } else if (spent.compareTo(budgetAmount) < 0) {
//                message = "🎉 Good Job! You saved " + budgetAmount.subtract(spent) +
//                        " in " + budget.getCategory() + " this month. Keep it up!";
//            } else {
//                message = "✅ Budget Met: You perfectly managed your budget for " + budget.getCategory() + ".";
//            }
//
//            // Append recommended budget
//            message += "\n📌 Recommended budget for next month: " + recommendedBudget;
//
//            NotificationModel notification = NotificationModel.builder()
//                    .userId(budget.getUserId())
//                    .subject("📊 Monthly Budget Summary for " + budget.getCategory())
//                    .msgBody(message)
//                    .status(Status.INITIALIZED)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            notificationQueue.enqueue(notification);
//            log.info("Queued Monthly Budget Notification for User: {} | Category: {} | Recommended Budget: {}",
//                    budget.getUserId(), budget.getCategory(), recommendedBudget);
//        }
//
//        log.info("Monthly Budget Notifications Processing Completed.");
//    }


    private void sendNotification(NotificationModel notification) {

        notification.setStatus(Status.SENT);
        notification.setUpdatedAt(LocalDateTime.now());
        // Log the notification details
        log.info("📢 Notification Sent: UserID={} | Subject = {} | Message='{}' | Time={}",
                notification.getUserId(),notification.getSubject(), notification.getMsgBody(), notification.getUpdatedAt());

    }
}
