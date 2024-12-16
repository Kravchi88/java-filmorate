INSERT INTO genres (genre_name) VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

INSERT INTO mpa_ratings (mpa_rating_name) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');

INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES
    ('user1@example.com', 'user1', 'User One', '1990-01-01'),
    ('user2@example.com', 'user2', 'User Two', '1992-02-02'),
    ('user3@example.com', 'user3', 'User Three', '1994-03-03'),
    ('user4@example.com', 'user4', 'User Four', '1996-04-04'),
    ('user5@example.com', 'user5', 'User Five', '1998-05-05');

INSERT INTO films (film_name, film_description, film_release_date, film_duration, film_mpa_rating_id) VALUES
    ('Film One', 'Description One', '2000-01-01', 120, 1),
    ('Film Two', 'Description Two', '2002-02-02', 90, 2),
    ('Film Three', 'Description Three', '2004-03-03', 110, 3),
    ('Film Four', 'Description Four', '2006-04-04', 95, 4),
    ('Film Five', 'Description Five', '2008-05-05', 100, 5);

INSERT INTO film_genres (film_id, genre_id) VALUES
    (1, 1), (1, 2),
    (2, 2), (2, 3),
    (3, 3), (3, 4),
    (4, 4), (4, 5),
    (5, 5), (5, 6);

INSERT INTO user_film_likes (user_id, film_id) VALUES
    (1, 1), (1, 2),
    (2, 3), (2, 4),
    (3, 5), (3, 1),
    (4, 2), (4, 3),
    (5, 4), (5, 5);

INSERT INTO user_friendships (requester_id, recipient_id, is_confirmed) VALUES
    (1, 2, true),
    (2, 3, true),
    (3, 4, false),
    (4, 5, true),
    (5, 1, false);