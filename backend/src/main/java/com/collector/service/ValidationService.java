package com.collector.service;

import com.collector.messaging.AnnoncePublisher;
import com.collector.model.Annonce;
import com.collector.repository.AnnonceRepository;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final AnnonceRepository annonceRepository;
    private final AnnoncePublisher annoncePublisher;
    private final Counter annoncesValidees;    
    private final Counter annoncesRejetees;  

    public void valider(Annonce annonce) {
        boolean valide = verifierAnnonce(annonce);

        if (valide) {
            annonce.setStatut(Annonce.StatutAnnonce.VALIDEE);
            annonce.setValidatedAt(LocalDateTime.now());
            annoncesValidees.increment();
            log.info("Annonce {} validée automatiquement", annonce.getId());
        } else {
            annonce.setStatut(Annonce.StatutAnnonce.REJETEE);
            annoncesRejetees.increment();
            log.warn("Annonce {} rejetée automatiquement", annonce.getId());
        }

        annonceRepository.save(annonce);

        // Publie l'événement dans RabbitMQ
        annoncePublisher.publierEvenement(annonce);
    }

    // Règles de validation automatique
    private boolean verifierAnnonce(Annonce annonce) {
        // Titre obligatoire et pas trop court
        if (annonce.getTitre() == null || annonce.getTitre().length() < 5) {
            return false;
        }

        // Description obligatoire
        if (annonce.getDescription() == null || annonce.getDescription().isEmpty()) {
            return false;
        }

        // Prix positif et pas aberrant (max 50 000€)
        if (annonce.getPrix() == null
                || annonce.getPrix().compareTo(BigDecimal.ZERO) <= 0
                || annonce.getPrix().compareTo(new BigDecimal("50000")) > 0) {
            return false;
        }

        return true;
    }
}