package ru.practicum.filmorate.rating;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(path = "/mpa")
@AllArgsConstructor
@RestController
public class RatingController {

    private final RatingServiceImpl service;

    @GetMapping("/{ratingId}")
    public Rating getRatingById(@PathVariable Long ratingId) {
        return service.getRatingById(ratingId);
    }

    @GetMapping
    public List<Rating> getRatings() {
        return service.getRatings();
    }
}
