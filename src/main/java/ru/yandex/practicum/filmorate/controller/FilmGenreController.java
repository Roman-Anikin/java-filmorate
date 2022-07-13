package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.FilmGenreService;

import java.util.List;

@RestController
public class FilmGenreController {

    private final FilmGenreService service;

    public FilmGenreController(FilmGenreService service) {
        this.service = service;
    }

    @GetMapping("/genres/{id}")
    public FilmGenre getGenreById(@PathVariable Integer id) {
        return service.getGenreById(id);
    }

    @GetMapping("/genres")
    public List<FilmGenre> getGenres() {
        return service.getGenres();
    }
}
