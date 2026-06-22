package com.collector.controller;

import com.collector.dto.AnnonceDTO;
import com.collector.model.User;
import com.collector.repository.UserRepository;
import com.collector.service.AnnonceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/annonces")
@RequiredArgsConstructor
public class AnnonceController {

    private final AnnonceService annonceService;
    private final UserRepository userRepository;

    // GET /api/annonces — public, liste toutes les annonces validées
    @GetMapping
    public ResponseEntity<List<AnnonceDTO>> getAnnonces() {
        return ResponseEntity.ok(annonceService.getAnnoncesValidees());
    }

    // POST /api/annonces — nécessite d'être connecté en tant que vendeur
    @PostMapping
    public ResponseEntity<AnnonceDTO> creerAnnonce(
            @Valid @RequestBody AnnonceDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        // Récupère l'utilisateur connecté depuis le token Keycloak
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        // Crée l'utilisateur s'il n'existe pas encore en BDD
        User vendeur = userRepository.findById(keycloakId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    return userRepository.save(newUser);
                });

        AnnonceDTO created = annonceService.creerAnnonce(dto, vendeur);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/annonces/mes-annonces — annonces du vendeur connecté
    @GetMapping("/mes-annonces")
    public ResponseEntity<List<AnnonceDTO>> getMesAnnonces(
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(annonceService.getAnnoncesByVendeur(keycloakId));
    }
}