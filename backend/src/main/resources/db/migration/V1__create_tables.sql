CREATE TABLE IF NOT EXISTS users (
                                     keycloak_id VARCHAR(255) PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_keycloak_id VARCHAR(255) REFERENCES users(keycloak_id),
                                          roles VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS annonces (
                                        id BIGSERIAL PRIMARY KEY,
                                        titre VARCHAR(255) NOT NULL,
                                        description TEXT,
                                        prix DECIMAL(10,2) NOT NULL,
                                        frais_port DECIMAL(10,2) DEFAULT 0,
                                        statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
                                        categorie VARCHAR(50) NOT NULL,
                                        vendeur_id VARCHAR(255) REFERENCES users(keycloak_id),
                                        image_url VARCHAR(500),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        validated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGSERIAL PRIMARY KEY,
                                             destinataire_id VARCHAR(255) REFERENCES users(keycloak_id),
                                             message TEXT NOT NULL,
                                             type VARCHAR(50),
                                             is_lu BOOLEAN DEFAULT FALSE,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);