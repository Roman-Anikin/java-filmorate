package ru.yandex.practicum.filmorate.storage.film.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Primary
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate template;

    public FilmDbStorage(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        addFilmGenre(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? where " +
                "film_id = ?";
        template.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        template.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        addFilmGenre(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name AS r_name " +
                "FROM films AS f " +
                "INNER JOIN rating AS r ON f.rating_id = r.rating_id " +
                "ORDER BY film_id";
        List<Film> films = template.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film film : films) {
            setGenre(film);
        }
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name AS r_name " +
                "FROM films AS f " +
                "INNER JOIN rating AS r ON f.rating_id = r.rating_id " +
                "WHERE film_id = ?";
        Film film = template.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        setGenre(film);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        template.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        template.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name AS r_name " +
                "FROM films AS f " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) AS user_id FROM film_likes GROUP BY film_id ORDER BY user_id DESC) " +
                "AS fl ON f.film_id = fl.film_id " +
                "INNER JOIN rating AS r ON f.rating_id = r.rating_id " +
                "ORDER BY user_id DESC " +
                "LIMIT ?";
        List<Film> films = template.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        for (Film film : films) {
            setGenre(film);
        }
        return films;
    }

    private void addFilmGenre(@NotNull Film film) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (FilmGenre genre : film.getGenres()) {
            template.update(sql, film.getId(), genre.getId());

        }
    }

    private void setGenre(@NotNull Film film) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM films AS f " +
                "INNER JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "INNER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = ?";
        film.setGenres(template.query(sql, (rs, rowNum) ->
                        new FilmGenre(rs.getInt("genre_id"), rs.getString("name")),
                film.getId()));
    }

    private Film makeFilm(@NotNull ResultSet rs) throws SQLException {
        return new Film(rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new FilmRating(rs.getInt("rating_id"), rs.getString("r_name")));
    }
}
