package com.collector.messaging;

import com.collector.model.User;
import com.collector.repository.UserRepository;
import com.collector.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AnnonceConsumerTest {

    @Test
    void onAnnonceValidee_shouldCreateNotification() {

        NotificationService notificationService = mock(NotificationService.class);
        UserRepository userRepository = mock(UserRepository.class);

        AnnonceConsumer consumer = new AnnonceConsumer(notificationService, userRepository);

        String message = "{\"vendeurId\":\"user-123\",\"titre\":\"Jordan 1\"}";

        User user = new User();
        user.setKeycloakId("user-123");

        when(userRepository.findById("user-123"))
                .thenReturn(Optional.of(user));

        consumer.onAnnonceValidee(message);

        verify(notificationService, times(1))
                .creerNotification(
                        eq(user),
                        contains("Jordan 1"),
                        eq(com.collector.model.Notification.TypeNotification.ANNONCE_VALIDEE)
                );
    }
    @Test
    void onAnnonceRejetee_shouldCreateNotification() {

        NotificationService notificationService = mock(NotificationService.class);
        UserRepository userRepository = mock(UserRepository.class);

        AnnonceConsumer consumer = new AnnonceConsumer(notificationService, userRepository);

        String message = "{\"vendeurId\":\"user-123\",\"titre\":\"Jordan 1\"}";

        User user = new User();
        user.setKeycloakId("user-123");

        when(userRepository.findById("user-123"))
                .thenReturn(Optional.of(user));

        consumer.onAnnonceRejetee(message);

        verify(notificationService, times(1))
                .creerNotification(
                        eq(user),
                        contains("rejetée"),
                        eq(com.collector.model.Notification.TypeNotification.ANNONCE_REJETEE)
                );
    }
}