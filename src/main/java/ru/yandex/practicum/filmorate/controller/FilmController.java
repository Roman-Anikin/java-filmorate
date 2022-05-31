package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            film.setId(id++);
        }
        try {
            if (isReleaseDateValid(film.getReleaseDate())) {
                films.put(film.getId(), film);
                log.info("Добавлен фильм " + film);
                return new ResponseEntity<>(film, HttpStatus.OK);
            }
        } catch (ValidationException e) {
            log.info("Exception: " + e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            if (isReleaseDateValid(film.getReleaseDate()) && films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Обновлен фильм " + film);
                return new ResponseEntity<>(film, HttpStatus.OK);
            } else {
                log.info("Попытка обновить фильм {}, которого нет в списке", film);
            }
        } catch (ValidationException e) {
            log.info("Exception: " + e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private boolean isReleaseDateValid(@NotNull LocalDate date) {
        if (date.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года. Переданная дата: "
                    + date);
        }
        return true;
    }
}
