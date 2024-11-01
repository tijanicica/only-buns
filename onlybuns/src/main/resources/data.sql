-- Ubacivanje lokacija
INSERT INTO location (latitude, longitude, street_number, street_name, city, country)
VALUES (45.2671, 19.8335, 10, 'Ulica 1', 'Novi Sad', 'Srbija');

INSERT INTO location (latitude, longitude, street_number, street_name, city, country)
VALUES (45.2510, 19.8451, 20, 'Ulica 2', 'Novi Sad', 'Srbija');

INSERT INTO location (latitude, longitude, street_number, street_name, city, country)
VALUES (45.2455, 19.8284, 30, 'Ulica 3', 'Novi Sad', 'Srbija');

-- Ubacivanje korisnika sa definisanim datumima
INSERT INTO registered_user (email, username, password, first_name, last_name, location_id, is_active, registration_date, activation_date, last_login_date, is_admin, followers_number)
VALUES ('tijana@example.com', 'tijana', 'hashedPassword1', 'Tijana', 'Petrović', 1, TRUE, '2024-10-20 12:00:00', '2024-10-25 14:00:00', '2024-10-30 10:00:00', FALSE, 100);

INSERT INTO registered_user (email, username, password, first_name, last_name, location_id, is_active, registration_date, activation_date, last_login_date, is_admin, followers_number)
VALUES ('marko@example.com', 'marko', 'hashedPassword2', 'Marko', 'Marković', 2, TRUE, '2024-10-18 09:30:00', '2024-10-23 11:00:00', '2024-10-30 09:00:00', FALSE, 150);

INSERT INTO registered_user (email, username, password, first_name, last_name, location_id, is_active, registration_date, activation_date, last_login_date, is_admin, followers_number)
VALUES ('jelena@example.com', 'jelena', 'hashedPassword3', 'Jelena', 'Jovanović', 3, TRUE, '2024-10-15 08:45:00', '2024-10-20 13:30:00', '2024-10-29 15:00:00', FALSE, 200);

-- Ubacivanje objava sa vremenima nakon activation_date
INSERT INTO post (description, photo, location_id, created_at, user_id)
VALUES ('Moj novi zeka! 🐰', '/images/bunny1.jpg', 1, '2024-10-26 15:00:00', 1);

INSERT INTO post (description, photo, location_id, created_at, user_id)
VALUES ('Pogledajte kako moj zeka skace! 🐇', '/images/bunny2.jpg', 2, '2024-10-24 12:30:00', 2);

INSERT INTO post (description, photo, location_id, created_at, user_id)
VALUES ('Još jedno divno jutro sa mojim zekama', '/images/bunny3.jpg', 3, '2024-10-21 10:15:00', 3);

INSERT INTO post (description, photo, location_id, created_at, user_id)
VALUES ('Zečevi u akciji! 🐰🐰', '/images/bunny4.jpg', 1, '2024-10-27 09:00:00', 1);

INSERT INTO post (description, photo, location_id, created_at, user_id)
VALUES ('Najslađi zeka ikada!', '/images/bunny5.jpg', 2, '2024-10-26 17:20:00', 2);
