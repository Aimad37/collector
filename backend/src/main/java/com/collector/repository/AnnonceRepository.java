package com.collector.repository;

import com.collector.model.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {

    // Trouve toutes les annonces d'un vendeur
    List<Annonce> findByVendeurKeycloakId(String vendeurId);

    // Trouve toutes les annonces par statut
    List<Annonce> findByStatut(Annonce.StatutAnnonce statut);

    // Trouve toutes les annonces par catégorie
    List<Annonce> findByCategorie(Annonce.Categorie categorie);

    // Trouve les annonces validées d'une catégorie
    List<Annonce> findByCategorieAndStatut(
            Annonce.Categorie categorie,
            Annonce.StatutAnnonce statut
    );
}