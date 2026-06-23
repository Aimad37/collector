package com.collector.messaging;

import com.collector.model.Annonce;
import com.collector.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

class AnnoncePublisherTest {

    @Test
    void publierEvenement_shouldSendRabbitMessage() {

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        AnnoncePublisher publisher = new AnnoncePublisher(rabbitTemplate);

        User user = new User();
        user.setKeycloakId("user-123");

        Annonce annonce = new Annonce();
        annonce.setId(1L);
        annonce.setTitre("Jordan 1");
        annonce.setStatut(Annonce.StatutAnnonce.VALIDEE);
        annonce.setVendeur(user);

        publisher.publierEvenement(annonce);

        verify(rabbitTemplate, times(1))
                .convertAndSend(
                        eq(AnnoncePublisher.EXCHANGE),
                        eq(AnnoncePublisher.ROUTING_KEY_VALIDEE),
                        contains("Jordan 1")
                );
    }
}