INSERT INTO genres (genre_name)
SELECT 'Комедия'
    WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Комедия');

INSERT INTO genres (genre_name)
SELECT 'Драма'
    WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Драма');

INSERT INTO genres (genre_name)
SELECT 'Мультфильм'
    WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Мультфильм');

INSERT INTO genres (genre_name)
SELECT 'Триллер'
    WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Триллер');

INSERT INTO genres (genre_name)
SELECT 'Документальный'
    WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Документальный');

INSERT INTO genres (genre_name)
SELECT 'Боевик'
    WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Боевик');

INSERT INTO mpa_ratings (mpa_rating_name)
SELECT 'G'
    WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_name = 'G');

INSERT INTO mpa_ratings (mpa_rating_name)
SELECT 'PG'
    WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_name = 'PG');

INSERT INTO mpa_ratings (mpa_rating_name)
SELECT 'PG-13'
    WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_name = 'PG-13');

INSERT INTO mpa_ratings (mpa_rating_name)
SELECT 'R'
    WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_name = 'R');

INSERT INTO mpa_ratings (mpa_rating_name)
SELECT 'NC-17'
    WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_name = 'NC-17');

