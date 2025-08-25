-- Brišemo prethodne unose da izbegnemo duplikate pri svakom pokretanju (opciono, ali korisno za testiranje)
-- Napomena: Redosled brisanja je važan zbog stranih ključeva!
DELETE FROM chatroom_participants;
DELETE FROM chat_message;
DELETE FROM chat_room;
-- DELETE FROM registered_user; -- Ovu liniju otkomentariši samo ako želiš da obrišeš SVE korisnike

-- Dodajemo nekoliko korisnika. Lozinka za sve je "password" (enkodovano).
-- Enkodovana vrednost za "password" je: $2a$10$YourEncodedPasswordHashHere (GENERISATI NOVU!)
-- Za potrebe primera, koristićemo privremeni hash. U realnosti, svaki korisnik bi imao svoj.
-- Hash za "password": $2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W
INSERT INTO registered_user (id, email, username, first_name, last_name, password, is_active, is_admin, followers_number, registration_date) VALUES
                                                                                                                                                 (1, 'iva_user@gmail.com', 'iva', 'Iva', 'Ivanovic', 'sifra123', true, true, 0, NOW()),
                                                                                                                                                 (2, 'marko@example.com', 'marko', 'Marko', 'Markovic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, NOW()),
                                                                                                                                                 (3, 'jelena@example.com', 'jelena', 'Jelena', 'Jelenic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, NOW()),
                                                                                                                                                 (4, 'petar@example.com', 'petar', 'Petar', 'Petrovic', '$2a$10$y.Cr3O58v2.531j3eO.s5.O23g31.DWS.u36a.GW8zz.n37.s.h6W', true, false, 0, NOW())
ON CONFLICT (id) DO UPDATE SET email = EXCLUDED.email, username = EXCLUDED.username;

-- Kreiramo jednu testnu GRUPNU sobu
INSERT INTO chat_room (id, name, admin_id, is_group_chat) VALUES
    (101, 'ISA Projekat Grupa', 1, true)
ON CONFLICT (id) DO NOTHING;

-- Dodajemo učesnike u tu GRUPNU sobu
INSERT INTO chatroom_participants (chatroom_id, user_id) VALUES
                                                             (101, 1), -- Admin (Iva)
                                                             (101, 3)  -- Član (Jelena)
ON CONFLICT (chatroom_id, user_id) DO NOTHING;

-- Dodajemo nekoliko testnih poruka u GRUPNU sobu
INSERT INTO chat_message (content, sent_at, sender_id, chat_room_id) VALUES
                                                                         ('Ćao svima, dobrodošli u grupu!', NOW() - INTERVAL '5 minute', 1, 101),
                                                                         ('Zdravo!', NOW() - INTERVAL '4 minute', 3, 101);

-- Kreiramo jedan testni PRIVATNI čet između korisnika 1 (Iva) i 2 (Marko)
INSERT INTO chat_room (id, name, is_group_chat) VALUES
    (102, 'iva - marko', false)
ON CONFLICT (id) DO NOTHING;

-- Dodajemo učesnike u taj PRIVATNI čet
INSERT INTO chatroom_participants (chatroom_id, user_id) VALUES
                                                             (102, 1),
                                                             (102, 2)
ON CONFLICT (chatroom_id, user_id) DO NOTHING;

-- Dodajemo testnu poruku u PRIVATNI čet
INSERT INTO chat_message (content, sent_at, sender_id, chat_room_id) VALUES
    ('Hej Marko, kako si?', NOW() - INTERVAL '10 minute', 1, 102);

-- Podešavamo sekvence da počnu od većeg broja da ne bi došlo do konflikta ID-jeva
SELECT setval('chat_room_id_seq', (SELECT MAX(id) FROM chat_room));
SELECT setval('registered_user_id_seq', (SELECT MAX(id) FROM registered_user));