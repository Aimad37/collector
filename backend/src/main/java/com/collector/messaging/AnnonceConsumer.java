package com.collector.messaging;

import com.collector.model.Notification;
import com.collector.model.User;
import com.collector.repository.UserRepository;
import com.collector.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnonceConsumer {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @RabbitListener(queues = "annonce.validee.queue")
    public void onAnnonceValidee(String message) {
        log.info("Message reçu (validée) : {}", message);

        // Extrait le vendeurId du message
        String vendeurId = extraireVendeurId(message);
        String titre = extraireTitre(message);

        userRepository.findById(vendeurId).ifPresent(vendeur -> {
            notificationService.creerNotification(
                    vendeur,
                    "Votre annonce \"" + titre + "\" a été validée et est maintenant en ligne !",
                    Notification.TypeNotification.ANNONCE_VALIDEE
            );
        });
    }

    @RabbitListener(queues = "annonce.rejetee.queue")
    public void onAnnonceRejetee(String message) {
        log.info("Message reçu (rejetée) : {}", message);

        String vendeurId = extraireVendeurId(message);
        String titre = extraireTitre(message);

        userRepository.findById(vendeurId).ifPresent(vendeur -> {
            notificationService.creerNotification(
                    vendeur,
                    "Votre annonce \"" + titre + "\" a été rejetée. Vérifiez les informations.",
                    Notification.TypeNotification.ANNONCE_REJETEE
            );
        });
    }

    // Extrait le vendeurId du message JSON simplement
    private String extraireVendeurId(String message) {
        String key = "\"vendeurId\":\"";
        int start = message.indexOf(key) + key.length();
        int end = message.indexOf("\"", start);
        return message.substring(start, end);
    }

    private String extraireTitre(String message) {
        String key = "\"titre\":\"";
        int start = message.indexOf(key) + key.length();
        int end = message.indexOf("\"", start);
        return message.substring(start, end);
    }
}