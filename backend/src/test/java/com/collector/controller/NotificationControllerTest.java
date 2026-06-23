package com.collector.controller;

import com.collector.model.Notification;
import com.collector.model.User;
import com.collector.repository.NotificationRepository;
import com.collector.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {

        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.rabbitmq.host=localhost",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/collector",

        // ✅ Cloudinary (les 2 formats pour éviter crash)
        "cloudinary.cloud-name=test",
        "cloudinary.api-key=test",
        "cloudinary.api-secret=test",
        "CLOUDINARY_CLOUD_NAME=test",
        "CLOUDINARY_API_KEY=test",
        "CLOUDINARY_API_SECRET=test"
})
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setKeycloakId("user-123");
        user.setUsername("testuser");
        user.setEmail("test@collector.com");
        userRepository.save(user);

        Notification n1 = new Notification();
        n1.setDestinataire(user);
        n1.setMessage("Notification 1");
        n1.setType(Notification.TypeNotification.NOUVELLE_ANNONCE);
        n1.setLu(false);
        n1.setCreatedAt(LocalDateTime.now());

        Notification n2 = new Notification();
        n2.setDestinataire(user);
        n2.setMessage("Notification 2");
        n2.setType(Notification.TypeNotification.CHANGEMENT_PRIX);
        n2.setLu(false);
        n2.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(n1);
        notificationRepository.save(n2);
    }
    @BeforeEach
    void clean() {
        notificationRepository.deleteAll();
    }
    @Test
    void getNotifications_shouldReturnUserNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .with(jwt().jwt(j -> j
                                .subject("user-123")
                                .issuer("http://localhost:8180/realms/collector")
                                .claim("preferred_username", "testuser")
                                .claim("email", "test@collector.com")
                        ))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Notification 1"));
    }

    @Test
    void marquerCommeLue_shouldMarkNotificationAsRead() throws Exception {

        Notification notif = notificationRepository.findAll().get(0);

        mockMvc.perform(put("/api/notifications/" + notif.getId() + "/lue")
                        .with(jwt().jwt(j -> j
                                .subject("user-123")
                                .issuer("http://localhost:8180/realms/collector")
                        ))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Notification updated = notificationRepository.findById(notif.getId()).orElseThrow();

        org.junit.jupiter.api.Assertions.assertTrue(updated.isLu());
    }
    @Test
    void getNotifications_withoutJwt_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void markAsRead_withoutJwt_shouldReturn401() throws Exception {
        mockMvc.perform(put("/api/notifications/1/lue"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getNotifications_shouldReturnEmptyList() throws Exception {

        notificationRepository.deleteAll();

        mockMvc.perform(get("/api/notifications")
                .with(jwt().jwt(j -> j.subject("user-123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}