package com.collector.service;

import com.collector.dto.NotificationDTO;
import com.collector.model.Notification;
import com.collector.model.User;
import com.collector.repository.NotificationRepository;
import com.collector.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Crée une notification pour un utilisateur
    public void creerNotification(User destinataire, String message,
                                  Notification.TypeNotification type) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setMessage(message);
        notification.setType(type);

        notificationRepository.save(notification);
        log.info("Notification créée pour {}", destinataire.getUsername());
    }

    // Récupère toutes les notifications d'un utilisateur
    public List<NotificationDTO> getNotifications(String userId) {
        return notificationRepository
                .findByDestinataireKeycloakId(userId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    // Marque une notification comme lue
    public void marquerCommeLue(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
    }
}