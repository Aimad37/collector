package com.collector.controller;

import com.collector.model.Annonce;
import com.collector.model.User;
import com.collector.repository.AnnonceRepository;
import com.collector.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

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
    "cloudinary.cloud-name=test",
    "cloudinary.api-key=test",
    "cloudinary.api-secret=test"
})
class AnnonceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        annonceRepository.deleteAll();
        userRepository.deleteAll();

        // Crée un user en BDD
        User user = new User();
        user.setKeycloakId("user-123");
        user.setUsername("testuser");
        user.setEmail("test@collector.com");
        userRepository.save(user);

        // Crée une annonce validée en BDD
        Annonce annonce = new Annonce();
        annonce.setTitre("Nike Air Jordan 1 Chicago 1985");
        annonce.setDescription("Paire originale en très bon état");
        annonce.setPrix(new BigDecimal("1500.00"));
        annonce.setFraisPort(new BigDecimal("10.00"));
        annonce.setCategorie(Annonce.Categorie.SNEAKERS);
        annonce.setStatut(Annonce.StatutAnnonce.VALIDEE);
        annonce.setVendeur(user);
        annonceRepository.save(annonce);
    }

    @Test
    void getAnnonces_shouldReturnAnnoncesFromDatabase() throws Exception {
        mockMvc.perform(get("/api/annonces")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titre")
                    .value("Nike Air Jordan 1 Chicago 1985"))
                .andExpect(jsonPath("$[0].statut").value("VALIDEE"));
    }

    @Test
    void getMesAnnonces_shouldReturnOnlyMyAnnonces() throws Exception {
        mockMvc.perform(get("/api/annonces/mes-annonces")
                .with(jwt().jwt(j -> j
                    .subject("user-123")
                    .claim("preferred_username", "testuser")
                    .claim("email", "test@collector.com")
                ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].vendeurUsername").value("testuser"));
    }

    @Test
    void getAnnonces_shouldReturnEmptyWhenNoAnnonces() throws Exception {
        annonceRepository.deleteAll();

        mockMvc.perform(get("/api/annonces")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}