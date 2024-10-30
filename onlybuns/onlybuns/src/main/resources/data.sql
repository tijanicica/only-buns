INSERT INTO location (latitude, longitude, street_number, street_name, city, country) VALUES (45.2671, 19.8335, 10, 'Ulica 1', 'Novi Sad', 'Srbija'),
(45.2510, 19.8451, 20, 'Ulica 2', 'Novi Sad', 'Srbija'),
(45.2455, 19.8284, 30, 'Ulica 3', 'Novi Sad', 'Srbija');

INSERT INTO registered_user (email, username, password, first_name, last_name, location_id, is_active, registration_date, last_login_date, is_admin, followers_number) VALUES
('tijana@example.com', 'tijana', 'hashedPassword1', 'Tijana', 'Petrović', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 100),
('marko@example.com', 'marko', 'hashedPassword2', 'Marko', 'Marković', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 150),
('jelena@example.com', 'jelena', 'hashedPassword3', 'Jelena', 'Jovanović', 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 200);