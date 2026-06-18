package com.collector.service;

import com.collector.messaging.AnnoncePublisher;
import com.collector.model.Annonce;
import com.collector.model.User;
import com.collector.repository.AnnonceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private AnnoncePublisher annoncePublisher;

    @InjectMocks
    private ValidationService validationService;

    private User vendeur;

    @BeforeEach
    void setUp() {
        vendeur = new User();
        vendeur.setKeycloakId("user-123");
        vendeur.setUsername("testuser");

        when(annonceRepository.save(any(Annonce.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(annoncePublisher).publierEvenement(any(Annonce.class));
    }

    @Test
    void valider_shouldValiderAnnonceValide() {
        // GIVEN
        Annonce annonce = new Annonce();
        annonce.setTitre("Nike Air Jordan 1985");
        annonce.setDescription("Paire originale en très bon état");
        annonce.setPrix(new BigDecimal("1500.00"));
        annonce.setVendeur(vendeur);

        // WHEN
        validationService.valider(annonce);

        // THEN
        assertEquals(Annonce.StatutAnnonce.VALIDEE, annonce.getStatut());
        assertNotNull(annonce.getValidatedAt());
    }

    @Test
    void valider_shouldRejeterAnnonceAvecTitreTropCourt() {
        // GIVEN
        Annonce annonce = new Annonce();
        annonce.setTitre("Nike"); // moins de 5 caractères
        annonce.setDescription("Description valide");
        annonce.setPrix(new BigDecimal("100.00"));
        annonce.setVendeur(vendeur);

        // WHEN
        validationService.valider(annonce);

        // THEN
        assertEquals(Annonce.StatutAnnonce.REJETEE, annonce.getStatut());
    }

    @Test
    void valider_shouldRejeterAnnonceAvecDescriptionVide() {
        // GIVEN
        Annonce annonce = new Annonce();
        annonce.setTitre("Nike Air Jordan");
        annonce.setDescription(""); // vide
        annonce.setPrix(new BigDecimal("100.00"));
        annonce.setVendeur(vendeur);

        // WHEN
        validationService.valider(annonce);

        // THEN
        assertEquals(Annonce.StatutAnnonce.REJETEE, annonce.getStatut());
    }

    @Test
    void valider_shouldRejeterAnnonceAvecPrixNegatif() {
        // GIVEN
        Annonce annonce = new Annonce();
        annonce.setTitre("Nike Air Jordan");
        annonce.setDescription("Description valide");
        annonce.setPrix(new BigDecimal("-10.00")); // négatif
        annonce.setVendeur(vendeur);

        // WHEN
        validationService.valider(annonce);

        // THEN
        assertEquals(Annonce.StatutAnnonce.REJETEE, annonce.getStatut());
    }

    @Test
    void valider_shouldRejeterAnnonceAvecPrixTropEleve() {
        // GIVEN
        Annonce annonce = new Annonce();
        annonce.setTitre("Nike Air Jordan");
        annonce.setDescription("Description valide");
        annonce.setPrix(new BigDecimal("99999.00")); // > 50000
        annonce.setVendeur(vendeur);

        // WHEN
        validationService.valider(annonce);

        // THEN
        assertEquals(Annonce.StatutAnnonce.REJETEE, annonce.getStatut());
    }
}