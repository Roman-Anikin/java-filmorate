package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Long id = 1L;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(Long filmId, Long userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                getFilmById(filmId).getLikes().add(userId);
                log.info("Пользователь {} поставил лайк фильму {}", userStorage.getUsers().get(userId),
                        getFilmById(filmId));
                return getFilmById(filmId);
            } else {
                throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
            }
        } else {
            throw new ObjectNotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    public Film removeLike(Long filmId, Long userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                getFilmById(filmId).getLikes().remove(userId);
                log.info("Пользователь {} удалил лайк к фильму {}", userStorage.getUsers().get(userId),
                        getFilmById(filmId));
                return getFilmById(filmId);
            } else {
                throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
            }
        } else {
            throw new ObjectNotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        if (count > getFilms().size()) {
            count = getFilms().size();
        }
        List<Film> popularFilms = getFilms();
        popularFilms.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());
        return popularFilms.subList(0, count);
    }

    public Film getFilmById(Long id) {
        if (filmStorage.getFilms().containsKey(id)) {
            return filmStorage.getFilms().get(id);
        } else {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    public Film addFilm(Film film) {
        if (isReleaseDateValid(film.getReleaseDate()) && !filmStorage.getFilms().containsKey(film.getId())) {
            if (film.getId() == null) {
                film.setId(id++);
            }
            filmStorage.addFilm(film);
            log.info("Добавлен фильм " + film);
            return filmStorage.addFilm(film);
        } else {
            throw new ObjectAlreadyExistException("Попытка добавить фильм {}, который уже есть в списке " + film);
        }
    }

    public Film updateFilm(Film film) {
        if (isReleaseDateValid(film.getReleaseDate()) && filmStorage.getFilms().containsKey(film.getId())) {
            log.info("Обновлен фильм " + film);
            return filmStorage.updateFilm(film);
        } else {
            throw new ObjectNotFoundException("Попытка обновить фильм {}, которого нет в списке " + film);
        }
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    private boolean isReleaseDateValid(@NotNull LocalDate date) {
        if (date.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года. Переданная дата: "
                    + date);
        }
        return true;
    }
}
