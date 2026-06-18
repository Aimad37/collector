package com.collector.repository;

import com.collector.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Trouve toutes les notifications d'un utilisateur
    List<Notification> findByDestinataireKeycloakId(String userId);

    // Trouve les notifications non lues d'un utilisateur
    List<Notification> findByDestinataireKeycloakIdAndLuFalse(String userId);
}