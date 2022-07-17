package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreDao;

import java.util.List;

@Component
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final JdbcTemplate template;

    public FilmGenreDaoImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public FilmGenre getGenreById(Integer id) {
        return template.queryForObject("select * from genres where genre_id = ?",
                (rs, rowNum) ->
                        new FilmGenre(rs.getInt("genre_id"), rs.getString("name")), id);
    }

    @Override
    public List<FilmGenre> getGenres() {
        return template.query("select * from genres order by genre_id",
                (rs, rowNum) ->
                        new FilmGenre(rs.getInt("genre_id"), rs.getString("name")));
    }
}
