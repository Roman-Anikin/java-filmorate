package ru.practicum.filmorate.film;

import java.util.List;

public interface FilmDao {

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(Long filmId);

    void saveLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);

}
