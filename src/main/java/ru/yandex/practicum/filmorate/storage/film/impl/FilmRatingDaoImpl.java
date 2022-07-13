package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmRatingDao;

import java.util.List;

@Component
public class FilmRatingDaoImpl implements FilmRatingDao {

    private final JdbcTemplate template;

    public FilmRatingDaoImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public FilmRating getRatingById(Integer id) {
        return template.queryForObject("select * from rating where rating_id = ?",
                (rs, rowNum) ->
                        new FilmRating(rs.getInt("rating_id"), rs.getString("name")), id);
    }

    @Override
    public List<FilmRating> getRatings() {
        return template.query("select * from rating order by rating_id",
                (rs, rowNum) ->
                        new FilmRating(rs.getInt("rating_id"), rs.getString("name")));
    }
}
