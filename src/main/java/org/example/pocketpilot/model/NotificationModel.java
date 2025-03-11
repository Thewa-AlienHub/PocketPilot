package org.example.pocketpilot.model;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.example.pocketpilot.enums.NotificationType;
import org.example.pocketpilot.enums.common.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationModel {
    private ObjectId userId;
    private String subject;
    private String msgBody;
    private NotificationType type;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
