package com.collector.messaging;

import com.collector.model.Annonce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnoncePublisher {

    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE = "collector.exchange";
    public static final String ROUTING_KEY_VALIDEE = "annonce.validee";
    public static final String ROUTING_KEY_REJETEE = "annonce.rejetee";

    public void publierEvenement(Annonce annonce) {
        String message = buildMessage(annonce);
        String routingKey = annonce.getStatut() == Annonce.StatutAnnonce.VALIDEE
                ? ROUTING_KEY_VALIDEE
                : ROUTING_KEY_REJETEE;

        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, message);
        log.info("Événement publié dans RabbitMQ : {} pour annonce {}",
                routingKey, annonce.getId());
    }

    private String buildMessage(Annonce annonce) {
        return String.format(
                "{\"annonceId\":%d,\"titre\":\"%s\",\"statut\":\"%s\",\"vendeurId\":\"%s\"}",
                annonce.getId(),
                annonce.getTitre(),
                annonce.getStatut(),
                annonce.getVendeur().getKeycloakId()
        );
    }
}