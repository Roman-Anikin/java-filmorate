package ru.yandex.practicum.filmorate.storage.film.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.FilmGenreService;
import ru.yandex.practicum.filmorate.service.FilmRatingService;
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
    private final FilmGenreService genreService;
    private final FilmRatingService ratingService;

    public FilmDbStorage(JdbcTemplate template, FilmGenreService service, FilmRatingService ratingService) {
        this.template = template;
        this.genreService = service;
        this.ratingService = ratingService;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "insert into films (name, description, release_date, duration, rating_id) values (?, ?, ?, ?, ?)";
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
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? where " +
                "film_id = ?";
        template.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        template.update("delete from film_genre where film_id = ?", film.getId());
        addFilmGenre(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from films order by film_id";
        List<Film> films = template.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film film : films) {
            setGenre(film);
        }
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "select * from films where film_id = ?";
        Film film = template.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        setGenre(film);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "insert into film_likes (film_id, user_id) values (?, ?)";
        template.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "delete from film_likes where film_id = ? and user_id = ?";
        template.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "select * from films where film_id in " +
                "(select film_id from " +
                "(select distinct f.film_id, count(fl.user_id) from films as f " +
                "left join film_likes as fl on f.film_id = fl.film_id " +
                "group by f.film_id, fl.user_id order by count(fl.user_id) desc) " +
                "limit ?)";
        List<Film> films = template.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        for (Film film : films) {
            setGenre(film);
        }
        return films;
    }

    private void addFilmGenre(@NotNull Film film) {
        String sql = "insert into film_genre (film_id, genre_id) values (?, ?)";
        for (FilmGenre genre : film.getGenres()) {
            if (genreService.getGenreById(genre.getId()) != null) {
                template.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private void setGenre(@NotNull Film film) {
        String sql = "select * from genres where genre_id in " +
                "(select genre_id from film_genre where film_id = ?)";
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
                ratingService.getRatingById(rs.getInt("rating_id")));
    }
}
