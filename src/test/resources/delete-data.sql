DELETE FROM films;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
DELETE FROM film_genre;
DELETE FROM film_likes;
DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
DELETE FROM user_friends;