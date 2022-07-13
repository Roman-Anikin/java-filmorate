package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.FilmRatingService;

import java.util.List;

@RestController
public class FilmRatingController {

    private final FilmRatingService service;

    public FilmRatingController(FilmRatingService service) {
        this.service = service;
    }

    @GetMapping("/mpa/{id}")
    public FilmRating getRatingById(@PathVariable Integer id) {
        return service.getRatingById(id);
    }

    @GetMapping("/mpa")
    public List<FilmRating> getRatings() {
        return service.getRatings();
    }
}
