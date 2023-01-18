package ru.practicum.filmorate.film;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RequestMapping(path = "/films")
@AllArgsConstructor
@Validated
@RestController
public class FilmController {

    private final FilmServiceImpl filmServiceImpl;

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmServiceImpl.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmServiceImpl.updateFilm(film);
    }

    @GetMapping
    public List<Film> get() {
        return filmServiceImpl.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return filmServiceImpl.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmServiceImpl.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmServiceImpl.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmServiceImpl.getPopularFilms(count);
    }
}