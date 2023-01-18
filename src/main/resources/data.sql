DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

DELETE FROM films;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

DELETE FROM ratings;
ALTER TABLE ratings ALTER COLUMN rating_id RESTART WITH 1;

DELETE FROM genres;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;

INSERT INTO ratings (name) VALUES ('G');
INSERT INTO ratings (name) VALUES ('PG');
INSERT INTO ratings (name) VALUES ('PG-13');
INSERT INTO ratings (name) VALUES ('R');
INSERT INTO ratings (name) VALUES ('NC-17');

INSERT INTO genres (name) VALUES ('Комедия');
INSERT INTO genres (name) VALUES ('Драма');
INSERT INTO genres (name) VALUES ('Мультфильм');
INSERT INTO genres (name) VALUES ('Триллер');
INSERT INTO genres (name) VALUES ('Документальный');
INSERT INTO genres (name) VALUES ('Боевик');