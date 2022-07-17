package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.List;

public interface FilmRatingDao {

    FilmRating getRatingById(Integer id);

    List<FilmRating> getRatings();
}
