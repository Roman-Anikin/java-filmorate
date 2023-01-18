package ru.practicum.filmorate.genre;

import ru.practicum.filmorate.film.Film;

import java.util.List;

public interface GenreService {

    Genre getGenreById(Long genreId);

    List<Genre> getGenres();

    void saveFilmGenre(Film film);

}
