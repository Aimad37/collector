package com.collector.dto;

import com.collector.model.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private String message;
    private String type;
    private boolean lu;
    private LocalDateTime createdAt;

    // Convertit une entité Notification en DTO
    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setLu(notification.isLu());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}