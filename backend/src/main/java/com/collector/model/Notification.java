package com.collector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private User destinataire;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    @Column(name = "is_lu")
    private boolean lu = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TypeNotification {
        NOUVELLE_ANNONCE,
        ANNONCE_VALIDEE,
        ANNONCE_REJETEE,
        CHANGEMENT_PRIX
    }
}