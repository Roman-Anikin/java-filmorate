package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmGenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService, FilmGenreService filmGenreService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = filmGenreService;
    }

    @Override
    public Film add(Film film) {
        checkReleaseDate(film.getReleaseDate());
        checkGenres(film.getGenres());
        film = filmStorage.addFilm(film);
        log.info("Добавлен фильм " + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkReleaseDate(film.getReleaseDate());
        checkGenres(film.getGenres());
        if (getById(film.getId()) != null) {
            filmStorage.updateFilm(film);
            log.info("Обновлен фильм " + film);
            return film;
        } else {
            throw new ObjectNotFoundException("Попытка обновить фильм {}, которого нет в списке " + film);
        }
    }

    @Override
    public List<Film> get() {
        List<Film> films = filmStorage.getFilms();
        log.info("Получен список фильмов {}", films);
        return films;
    }

    @Override
    public Film getById(Long id) {
        try {
            Film film = filmStorage.getFilmById(id);
            log.info("Получен фильм {}", film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public void add(Long filmId, Long userId) {
        if (getById(filmId) != null && userService.getById(userId) != null) {
            filmStorage.addLike(filmId, userId);
            log.info("Пользователь {} поставил лайк фильму {}", userService.getById(userId), getById(filmId));
        }
    }

    @Override
    public void remove(Long filmId, Long userId) {
        if (getById(filmId) != null && userService.getById(userId) != null) {
            filmStorage.removeLike(filmId, userId);
            log.info("Пользователь {} удалил лайк к фильму {}", userService.getById(userId), getById(filmId));
        }
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        log.info("Получен список популярных фильмов {}", films);
        return films;
    }

    private void checkReleaseDate(@NotNull LocalDate date) {
        if (date.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года. Переданная дата: "
                    + date);
        }
    }

    private void checkGenres(@NotNull List<FilmGenre> genres) {
        Set<Integer> ids = new HashSet<>();
        for (FilmGenre genre : genres) {
            if (genreService.getGenreById(genre.getId()) != null) {
                ids.add(genre.getId());
            }
        }
        genres.clear();
        for (Integer i : ids) {
            genres.add(genreService.getGenreById(i));
        }
    }
}
