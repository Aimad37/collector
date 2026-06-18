package com.collector.dto;

import com.collector.model.Annonce;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AnnonceDTO {

    private Long id;
    private String titre;
    private String description;
    private String imageUrl;
    private BigDecimal prix;
    private BigDecimal fraisPort;
    private String categorie;
    private String statut;
    private String vendeurId;
    private String vendeurUsername;

    // Convertit une entité Annonce en DTO
    public static AnnonceDTO fromEntity(Annonce annonce) {
        AnnonceDTO dto = new AnnonceDTO();
        dto.setId(annonce.getId());
        dto.setTitre(annonce.getTitre());
        dto.setDescription(annonce.getDescription());
        dto.setImageUrl(annonce.getImageUrl());
        dto.setPrix(annonce.getPrix());
        dto.setFraisPort(annonce.getFraisPort());
        dto.setCategorie(annonce.getCategorie().name());
        dto.setStatut(annonce.getStatut().name());
        dto.setVendeurId(annonce.getVendeur().getKeycloakId());
        dto.setVendeurUsername(annonce.getVendeur().getUsername());
        return dto;
    }
}