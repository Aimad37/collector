INSERT INTO users (keycloak_id, username, email)
VALUES
    ('test-user-id-001', 'testuser', 'test@collector.com'),
    ('test-user-id-002', 'alice', 'alice@collector.com'),
    ('test-user-id-003', 'bob', 'bob@collector.com')
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_keycloak_id, roles)
VALUES
    ('test-user-id-001', 'VENDEUR'),
    ('test-user-id-001', 'ACHETEUR'),
    ('test-user-id-002', 'ACHETEUR'),
    ('test-user-id-003', 'VENDEUR')
ON CONFLICT DO NOTHING;

INSERT INTO annonces (titre, description, prix, frais_port, statut, categorie, vendeur_id, image_url)
VALUES
    (
        'Nike Air Jordan 1 Chicago 1985',
        'Paire originale en très bon état. Boîte d''origine incluse. Taille 42. Colorway Chicago OG.',
        1500.00, 10.00, 'VALIDEE', 'SNEAKERS', 'test-user-id-001',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/shoes/pair-of-shoes.jpg'
    ),
    (
        'Figurine Star Wars Dark Vador 1977',
        'Figurine Kenner originale 1977. Très rare. État collector. Avec sabre laser d''origine.',
        850.00, 8.00, 'VALIDEE', 'FIGURINES', 'test-user-id-003',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/animals/cat.jpg'
    ),
    (
        'Cassette VHS Ghostbusters 1984',
        'Cassette VHS originale américaine. Première édition. Fonctionne parfaitement.',
        45.00, 5.00, 'VALIDEE', 'AUTRE', 'test-user-id-003',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/landscapes/beach-boat.jpg'
    ),
    (
        'Vinyle Pink Floyd The Wall 1979',
        'Double vinyle original. Pochette en excellent état. Pressage UK first press.',
        120.00, 7.00, 'VALIDEE', 'VINYLES', 'test-user-id-001',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/music/vinyl.jpg'
    ),
    (
        'Game Boy Color Pokemon Edition',
        'Game Boy Color édition spéciale Pokemon. Complète avec boîte et notice originale.',
        280.00, 6.00, 'VALIDEE', 'JEUX_VIDEO', 'test-user-id-003',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/animals/dog.jpg'
    ),
    (
        'BD Tintin Au Tibet 1960',
        'Edition originale Casterman 1960. Très bon état. Dos carré.',
        200.00, 5.00, 'VALIDEE', 'BD_MANGAS', 'test-user-id-001',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/books/book.jpg'
    ),
    (
        'Adidas Superstar 1969 Original',
        'Paire vintage authentique. Shell toe intact. Taille 41.',
        320.00, 10.00, 'VALIDEE', 'SNEAKERS', 'test-user-id-003',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/shoes/boots.jpg'
    ),
    (
        'Figurine Optimus Prime G1 1984',
        'Transformers Generation 1 original. Complet avec accessoires.',
        450.00, 8.00, 'VALIDEE', 'FIGURINES', 'test-user-id-001',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/animals/three-dogs.jpg'
    ),
    (
        'Vinyle Michael Jackson Thriller 1982',
        'Vinyle original CBS Records 1982. Premier pressage américain.',
        95.00, 6.00, 'VALIDEE', 'VINYLES', 'test-user-id-003',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/music/guitar.jpg'
    ),
    (
        'Nintendo Game & Watch 1980',
        'Game & Watch Ball original 1980. Fonctionne parfaitement. Très rare.',
        380.00, 5.00, 'VALIDEE', 'JEUX_VIDEO', 'test-user-id-001',
        'https://res.cloudinary.com/demo/image/upload/v1/samples/animals/reindeer.jpg'
    )
ON CONFLICT DO NOTHING;