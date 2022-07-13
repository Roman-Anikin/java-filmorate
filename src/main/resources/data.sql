INSERT INTO rating (name) VALUES ('G') on conflict do nothing;
INSERT INTO rating (name) VALUES ('PG') on conflict do nothing;
INSERT INTO rating (name) VALUES ('PG-13') on conflict do nothing;
INSERT INTO rating (name) VALUES ('R') on conflict do nothing;
INSERT INTO rating (name) VALUES ('NC-17') on conflict do nothing;

INSERT INTO genres (name) VALUES ('Комедия') on conflict do nothing;
INSERT INTO genres (name) VALUES ('Драма') on conflict do nothing;
INSERT INTO genres (name) VALUES ('Мультфильм') on conflict do nothing;
INSERT INTO genres (name) VALUES ('Триллер') on conflict do nothing;
INSERT INTO genres (name) VALUES ('Документальный') on conflict do nothing;
INSERT INTO genres (name) VALUES ('Боевик') on conflict do nothing;