-- Brišemo stare podatke da izbegnemo duplikate
DELETE FROM comment;
DELETE FROM post;
DELETE FROM chatroom_participants;
DELETE FROM chat_message;
DELETE FROM chat_room;
DELETE FROM registered_user;
DELETE FROM location;

-- Podešavamo sekvence da krenu od početka
ALTER SEQUENCE registered_user_id_seq RESTART WITH 1;
ALTER SEQUENCE location_id_seq RESTART WITH 1;
ALTER SEQUENCE post_id_seq RESTART WITH 1;
ALTER SEQUENCE comment_id_seq RESTART WITH 1;
ALTER SEQUENCE chat_room_id_seq RESTART WITH 1;


-- === KORISNICI ===
-- Lozinka za sve je "password" (hash: $2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W)
INSERT INTO location (street_name, street_number, city, country, latitude, longitude) VALUES
                                                                                          ('Ulica 1', '1', 'Novi Sad', 'Srbija', 45.25, 19.83),
                                                                                          ('Ulica 2', '2', 'Beograd', 'Srbija', 44.78, 20.44),
                                                                                          ('Ulica 3', '3', 'Niš', 'Srbija', 43.32, 21.89),
                                                                                          ('Ulica 4', '4', 'Subotica', 'Srbija', 46.09, 19.66);

INSERT INTO registered_user (email, username, first_name, last_name, password, is_active, is_admin, followers_number, registration_date, activation_date, location_id) VALUES
                                                                                                                                                                           ('admin@buns.com', 'admin', 'Admin', 'Adminic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, true, 0, '2023-01-01 10:00:00', '2023-01-01 10:00:00', 1),
                                                                                                                                                                           ('user1@buns.com', 'user1', 'Pera', 'Peric', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, '2023-01-01 10:00:00', '2023-01-01 10:00:00', 2),
                                                                                                                                                                           ('user2@buns.com', 'user2', 'Mika', 'Mikic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, '2023-01-01 10:00:00', '2023-01-01 10:00:00', 3),
                                                                                                                                                                           ('user3_comments_only@buns.com', 'user3', 'Ana', 'Anic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, '2023-01-01 10:00:00', '2023-01-01 10:00:00', 4),
                                                                                                                                                                           ('user4_inactive@buns.com', 'user4', 'Zika', 'Zikic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, '2023-01-01 10:00:00', '2023-01-01 10:00:00', 1);

-- === PODACI ZA ANALITIKU SA FIKSNIM DATUMIMA ===

-- Godina 2023 (1 post, 1 komentar)
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Post iz 2023.', '/images/bunny1.jpg', 1, '2023-07-15 12:00:00', 1);
INSERT INTO comment (post_id, user_id, date, content) VALUES
    ((SELECT id FROM post WHERE description = 'Post iz 2023.'), 2, '2023-07-15 13:00:00', 'Komentar iz 2023.');

-- Godina 2024 (2 posta, 3 komentara)
-- -- Mesec Mart
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Post iz Marta 2024.', '/images/bunny2.jpg', 2, '2024-03-10 12:00:00', 2);
INSERT INTO comment (post_id, user_id, date, content) VALUES
    ((SELECT id FROM post WHERE description = 'Post iz Marta 2024.'), 1, '2024-03-10 14:00:00', 'Komentar iz Marta.');
-- -- Mesec Avgust
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Post iz Avgusta 2024.', '/images/bunny3.jpg', 3, '2024-08-01 12:00:00', 1);
INSERT INTO comment (post_id, user_id, date, content) VALUES
                                                          ((SELECT id FROM post WHERE description = 'Post iz Avgusta 2024.'), 2, '2024-08-01 13:00:00', 'Prvi kom iz Avgusta.'),
                                                          ((SELECT id FROM post WHERE description = 'Post iz Avgusta 2024.'), 3, '2024-08-02 15:00:00', 'Drugi kom iz Avgusta.');


-- Godina 2025 (4 posta, 4 komentara)
-- -- Mesec Maj
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Post iz Maja 2025.', '/images/bunny4.jpg', 4, '2025-05-20 12:00:00', 2);
INSERT INTO comment (post_id, user_id, date, content) VALUES
    ((SELECT id FROM post WHERE description = 'Post iz Maja 2025.'), 1, '2025-05-20 13:00:00', 'Komentar iz Maja.');
-- -- Mesec Avgust, Nedelja 2
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Avgust, nedelja 2, post 1', '/images/bunny5.jpg', 1, '2025-08-12 10:00:00', 1);
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Avgust, nedelja 2, post 2', '/images/bunny6.jpg', 2, '2025-08-14 11:00:00', 2);
INSERT INTO comment (post_id, user_id, date, content) VALUES
    ((SELECT id FROM post WHERE description = 'Avgust, nedelja 2, post 1'), 3, '2025-08-12 11:00:00', 'Komentar korisnika 3.');
-- -- Mesec Avgust, Nedelja 4
INSERT INTO post (description, photo, location_id, created_at, user_id) VALUES
    ('Avgust, nedelja 4', '/images/bunny8.jpg', 1, '2025-08-25 09:00:00', 1);
INSERT INTO comment (post_id, user_id, date, content) VALUES
                                                          ((SELECT id FROM post WHERE description = 'Avgust, nedelja 4'), 2, '2025-08-25 10:00:00', 'Komentar korisnika 2.'),
                                                          ((SELECT id FROM post WHERE description = 'Avgust, nedelja 4'), 3, '2025-08-25 11:00:00', 'Još jedan komentar korisnika 3.');


-- Podešavamo sekvence da počnu od većeg broja
SELECT setval('post_id_seq', (SELECT MAX(id) FROM post) + 1, false);
SELECT setval('comment_id_seq', (SELECT MAX(id) FROM comment) + 1, false);```
INSERT INTO registered_user (id, email, username, first_name, last_name, password, is_active, is_admin, followers_number, registration_date, activation_date, location_id) VALUES
    (6, 'ivajovanovic50@gmail.com', 'ivajovanovic50', 'Iva', 'Jovanovic', '$2a$10$Wd.T.H7.m1cR7x/WdJ0/n.HhYg5h3oYlUe.nJg8w.qg8.v8x.v5uO', true, true, 0, NOW(), NOW(), 1)
ON CONFLICT (id) DO UPDATE SET
                               email = EXCLUDED.email,
                               username = EXCLUDED.username,
                               first_name = EXCLUDED.first_name,
                               last_name = EXCLUDED.last_name,
                               password = EXCLUDED.password,
                               is_active = EXCLUDED.is_active,
                               is_admin = EXCLUDED.is_admin;