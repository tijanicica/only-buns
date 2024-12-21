

-- Test Post (created in the last 3 days)
INSERT INTO post (id, description, photo, location_id, created_at, user_id)
VALUES
    (30, 'Test post description', 'testphoto.jpg', 1, NOW() - INTERVAL '3 days', 1);

-- Test Comment (created in the last 2 days)
INSERT INTO comment (id, post_id, user_id, date, content)
VALUES
    (30, 1, 1, NOW() - INTERVAL '2 days', 'Test comment content');
