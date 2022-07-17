package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreDao {

    FilmGenre getGenreById(Integer id);

    List<FilmGenre> getGenres();
}
