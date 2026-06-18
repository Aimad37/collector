package com.collector.service;

import com.collector.dto.AnnonceDTO;
import com.collector.messaging.AnnoncePublisher;
import com.collector.model.Annonce;
import com.collector.model.User;
import com.collector.repository.AnnonceRepository;
import com.collector.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final AnnoncePublisher annoncePublisher;
    private final ValidationService validationService;

    // Crée une annonce et lance la validation automatique
    public AnnonceDTO creerAnnonce(AnnonceDTO dto, User vendeur) {
        Annonce annonce = new Annonce();
        annonce.setTitre(dto.getTitre());
        annonce.setDescription(dto.getDescription());
        annonce.setPrix(dto.getPrix());
        annonce.setFraisPort(dto.getFraisPort());
        annonce.setCategorie(Annonce.Categorie.valueOf(dto.getCategorie()));
        annonce.setVendeur(vendeur);
        annonce.setStatut(Annonce.StatutAnnonce.EN_ATTENTE);
        annonce.setImageUrl(dto.getImageUrl());
        // Sauvegarde en BDD
        Annonce saved = annonceRepository.save(annonce);
        log.info("Annonce créée avec id : {}", saved.getId());

        // Validation automatique
        validationService.valider(saved);

        return AnnonceDTO.fromEntity(saved);
    }

    // Récupère toutes les annonces validées
    public List<AnnonceDTO> getAnnoncesValidees() {
        return annonceRepository
                .findByStatut(Annonce.StatutAnnonce.VALIDEE)
                .stream()
                .map(AnnonceDTO::fromEntity)
                .toList();
    }

    // Récupère les annonces d'un vendeur
    public List<AnnonceDTO> getAnnoncesByVendeur(String vendeurId) {
        return annonceRepository
                .findByVendeurKeycloakId(vendeurId)
                .stream()
                .map(AnnonceDTO::fromEntity)
                .toList();
    }
}