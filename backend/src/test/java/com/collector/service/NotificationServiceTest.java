package com.collector.service;

import com.collector.dto.NotificationDTO;
import com.collector.model.Notification;
import com.collector.model.User;
import com.collector.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    private User destinataire;

    @BeforeEach
    void setUp() {
        destinataire = new User();
        destinataire.setKeycloakId("user-789");
        destinataire.setUsername("collector_fan");
    }

    @Test
    void creerNotification_shouldSaveNotificationWithCorrectFields() {
        // GIVEN
        String message = "Votre annonce a été validée !";
    
        Notification.TypeNotification type = Notification.TypeNotification.ANNONCE_VALIDEE;

        // WHEN
        notificationService.creerNotification(destinataire, message, type);

        // THEN
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(destinataire, savedNotification.getDestinataire());
        assertEquals(message, savedNotification.getMessage());
        assertEquals(type, savedNotification.getType());
        assertFalse(savedNotification.isLu()); 
    }

    @Test
    void getNotifications_shouldReturnMappedDtoList() {
        // GIVEN
        String userId = "user-789";
        
        Notification n1 = new Notification();
        n1.setId(1L);
        n1.setMessage("Msg 1");
        n1.setType(Notification.TypeNotification.ANNONCE_VALIDEE); // <-- AJOUTE CECI
        n1.setDestinataire(destinataire);
        
        Notification n2 = new Notification();
        n2.setId(2L);
        n2.setMessage("Msg 2");
        n2.setType(Notification.TypeNotification.NOUVELLE_ANNONCE); // <-- AJOUTE CECI
        n2.setDestinataire(destinataire);

        when(notificationRepository.findByDestinataireKeycloakId(userId)).thenReturn(List.of(n1, n2));

        // WHEN
        List<NotificationDTO> result = notificationService.getNotifications(userId);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByDestinataireKeycloakId(userId);
    }

    @Test
    void marquerCommeLue_shouldUpdateLuToTrue_whenNotificationExists() {
        // GIVEN
        Long notificationId = 42L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setLu(false);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // WHEN
        notificationService.marquerCommeLue(notificationId);

        // THEN
        assertTrue(notification.isLu());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void marquerCommeLue_shouldDoNothing_whenNotificationDoesNotExist() {
        // GIVEN
        Long notificationId = 99L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // WHEN
        notificationService.marquerCommeLue(notificationId);

        // THEN
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}