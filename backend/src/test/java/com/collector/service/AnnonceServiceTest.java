package com.collector.service;

import com.collector.dto.AnnonceDTO;
import com.collector.messaging.AnnoncePublisher;
import com.collector.model.Annonce;
import com.collector.model.User;
import com.collector.repository.AnnonceRepository;

import io.micrometer.core.instrument.Counter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private AnnoncePublisher annoncePublisher;

    @Mock
    private ValidationService validationService;

    @Mock
    private Counter annoncesCreees;

    @InjectMocks
    private AnnonceService annonceService;

    private User vendeur;
    private AnnonceDTO dto;

    @BeforeEach
    void setUp() {
        vendeur = new User();
        vendeur.setKeycloakId("user-123");
        vendeur.setUsername("testuser");
        vendeur.setEmail("test@collector.com");

        dto = new AnnonceDTO();
        dto.setTitre("Nike Air Jordan 1 Chicago 1985");
        dto.setDescription("Paire originale en très bon état");
        dto.setPrix(new BigDecimal("1500.00"));
        dto.setFraisPort(new BigDecimal("10.00"));
        dto.setCategorie("SNEAKERS");
        dto.setImageUrl("https://cloudinary.com/test.jpg");
    }

    @Test
    void creerAnnonce_shouldSaveAndReturnDTO() {
        // GIVEN
        Annonce savedAnnonce = new Annonce();
        savedAnnonce.setId(1L);
        savedAnnonce.setTitre(dto.getTitre());
        savedAnnonce.setDescription(dto.getDescription());
        savedAnnonce.setPrix(dto.getPrix());
        savedAnnonce.setFraisPort(dto.getFraisPort());
        savedAnnonce.setCategorie(Annonce.Categorie.SNEAKERS);
        savedAnnonce.setVendeur(vendeur);
        savedAnnonce.setStatut(Annonce.StatutAnnonce.EN_ATTENTE);
        savedAnnonce.setImageUrl(dto.getImageUrl());

        when(annonceRepository.save(any(Annonce.class))).thenReturn(savedAnnonce);
        doNothing().when(validationService).valider(any(Annonce.class));

        // WHEN
        AnnonceDTO result = annonceService.creerAnnonce(dto, vendeur);

        // THEN
        assertNotNull(result);
        assertEquals("Nike Air Jordan 1 Chicago 1985", result.getTitre());
        assertEquals(new BigDecimal("1500.00"), result.getPrix());
        verify(annonceRepository, times(1)).save(any(Annonce.class));
        verify(validationService, times(1)).valider(any(Annonce.class));
    }

    @Test
    void creerAnnonce_shouldCallValidationService() {
        // GIVEN
        Annonce savedAnnonce = new Annonce();
        savedAnnonce.setId(1L);
        savedAnnonce.setTitre(dto.getTitre());
        savedAnnonce.setDescription(dto.getDescription());
        savedAnnonce.setPrix(dto.getPrix());
        savedAnnonce.setFraisPort(dto.getFraisPort());
        savedAnnonce.setCategorie(Annonce.Categorie.SNEAKERS);
        savedAnnonce.setVendeur(vendeur);
        savedAnnonce.setStatut(Annonce.StatutAnnonce.EN_ATTENTE);

        when(annonceRepository.save(any(Annonce.class))).thenReturn(savedAnnonce);
        doNothing().when(validationService).valider(any(Annonce.class));

        // WHEN
        annonceService.creerAnnonce(dto, vendeur);

        // THEN
        verify(validationService, times(1)).valider(any(Annonce.class));
    }

    @Test
    void getAnnoncesValidees_shouldReturnOnlyValidees() {
        // GIVEN
        Annonce annonce1 = new Annonce();
        annonce1.setId(1L);
        annonce1.setTitre("Annonce 1");
        annonce1.setStatut(Annonce.StatutAnnonce.VALIDEE);
        annonce1.setCategorie(Annonce.Categorie.SNEAKERS);
        annonce1.setPrix(new BigDecimal("100"));
        annonce1.setFraisPort(BigDecimal.ZERO);
        annonce1.setVendeur(vendeur);

        when(annonceRepository.findByStatut(Annonce.StatutAnnonce.VALIDEE))
            .thenReturn(List.of(annonce1));

        // WHEN
        List<AnnonceDTO> result = annonceService.getAnnoncesValidees();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Annonce 1", result.get(0).getTitre());
    }
}