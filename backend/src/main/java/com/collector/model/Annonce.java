package com.collector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "annonces")
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private BigDecimal prix;

    @Column(name = "frais_port")
    private BigDecimal fraisPort = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAnnonce statut = StatutAnnonce.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "vendeur_id", nullable = false)
    private User vendeur;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    public enum StatutAnnonce {
        EN_ATTENTE,
        VALIDEE,
        REJETEE,
        VENDUE
    }

    public enum Categorie {
        SNEAKERS,
        FIGURINES,
        VINYLES,
        JEUX_VIDEO,
        BD_MANGAS,
        AUTRE
    }
}