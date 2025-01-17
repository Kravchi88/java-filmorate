DROP TABLE IF EXISTS user_film_likes CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS user_friendships CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS mpa_ratings CASCADE;
DROP TABLE IF EXISTS directors CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS genres (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_rating_id INT AUTO_INCREMENT PRIMARY KEY,
    mpa_rating_name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INT AUTO_INCREMENT PRIMARY KEY,
    director_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_name VARCHAR(50) NOT NULL,
    film_description VARCHAR(200),
    film_release_date DATE NOT NULL,
    film_duration INT,
    film_mpa_rating_id INT,
    film_director_id INT,
    FOREIGN KEY (film_mpa_rating_id) REFERENCES mpa_ratings(mpa_rating_id) ON DELETE CASCADE,
    FOREIGN KEY (film_director_id) REFERENCES directors(director_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL,
    user_login VARCHAR(50) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_film_likes (
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_friendships (
    requester_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    is_confirmed BOOLEAN NOT NULL,
    PRIMARY KEY (requester_id, recipient_id),
    FOREIGN KEY (requester_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(user_id) ON DELETE CASCADE
);