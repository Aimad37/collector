package com.collector.dto;

import com.collector.model.Annonce;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AnnonceDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 255, message = "Le titre doit faire entre 5 et 255 caractères")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    @DecimalMax(value = "50000", message = "Le prix ne peut pas dépasser 50 000 €")
    private BigDecimal prix;

    @PositiveOrZero(message = "Les frais de port ne peuvent pas être négatifs")
    private BigDecimal fraisPort;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    private String statut;
    private String vendeurId;
    private String vendeurUsername;
    private String imageUrl;

    public static AnnonceDTO fromEntity(Annonce annonce) {
        AnnonceDTO dto = new AnnonceDTO();
        dto.setId(annonce.getId());
        dto.setTitre(annonce.getTitre());
        dto.setDescription(annonce.getDescription());
        dto.setPrix(annonce.getPrix());
        dto.setFraisPort(annonce.getFraisPort());
        dto.setCategorie(annonce.getCategorie().name());
        dto.setStatut(annonce.getStatut().name());
        dto.setVendeurId(annonce.getVendeur().getKeycloakId());
        dto.setVendeurUsername(annonce.getVendeur().getUsername());
        dto.setImageUrl(annonce.getImageUrl());
        return dto;
    }
}