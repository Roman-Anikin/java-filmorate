package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String url = "/films";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getFilms() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("film")));
    }

    @Test
    public void addValidFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("film")))
                .andExpect(jsonPath("$.description", is("desc")))
                .andExpect(jsonPath("$.releaseDate", is("2000-10-10")))
                .andExpect(jsonPath("$.duration", is(5)));
    }

    @Test
    public void updateFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("new film");
        film2.setDescription("new film desc");
        film2.setReleaseDate(LocalDate.of(2000, 10, 10));
        film2.setDuration(15);

        mockRequest = putRequest(film2);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("new film")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("new film desc")));
    }

    @Test
    public void addFilmWithoutName() throws Exception {
        Film film = new Film();
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test

    public void addFilmWithLongDescription() throws Exception {
        Film film = new Film();
        film.setName("new film");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «своего отсутствия», стал кандидатом Коломбани.");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithWrongDate() throws Exception {
        Film film = new Film();
        film.setName("new film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1890, 3, 25));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithWrongDuration() throws Exception {
        Film film = new Film();
        film.setName("new film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(0);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void wrongFilmUpdate() throws Exception {
        Film film = new Film();
        film.setId(1L);
        film.setName("new film");
        film.setDescription("new film desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(15);

        MockHttpServletRequestBuilder mockRequest = putRequest(film);
        mockMvc.perform(mockRequest).andExpect(status().isInternalServerError());

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("film");
        film2.setDescription("film desc");
        film2.setReleaseDate(LocalDate.of(2000, 10, 10));
        film2.setDuration(15);

        mockRequest = putRequest(film2);
        mockMvc.perform(mockRequest).andExpect(status().isInternalServerError());
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(Film film) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(film));
    }

    private @NotNull MockHttpServletRequestBuilder putRequest(Film film) throws JsonProcessingException {
        return MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(film));
    }
}
