package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
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
        mockMvc.perform(mockRequest).andExpect(status().isNotFound());

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setId(2L);
        film2.setName("film");
        film2.setDescription("film desc");
        film2.setReleaseDate(LocalDate.of(2000, 10, 10));
        film2.setDuration(15);

        mockRequest = putRequest(film2);
        mockMvc.perform(mockRequest).andExpect(status().isNotFound());
    }

    @Test
    public void getFilmById() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.get(url + "/1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("film")));
    }

    @Test
    public void addLike() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/like/1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(1)))
                .andExpect(jsonPath("$.likes[0]", is(1)));
    }

    @Test
    public void removeLike() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/like/1");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.delete(url + "/1/like/1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", empty()));
    }

    @Test
    public void getPopularFilms() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user2 = new User();
        user2.setEmail("asdf@mail.com");
        user2.setLogin("asdfg");
        user2.setName("Name");
        user2.setBirthday(LocalDate.of(2000, 10, 10));

        mockRequest = postRequest(user2);
        mockMvc.perform(mockRequest);

        User user3 = new User();
        user3.setEmail("zxcv@mail.com");
        user3.setLogin("zxcvb");
        user3.setName("New name");
        user3.setBirthday(LocalDate.of(2010, 1, 1));

        mockRequest = postRequest(user3);
        mockMvc.perform(mockRequest);

        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setDuration(5);

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setName("new film");
        film2.setDescription("new film desc");
        film2.setReleaseDate(LocalDate.of(2000, 10, 10));
        film2.setDuration(15);

        mockRequest = postRequest(film2);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/like/1");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/like/2");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/like/3");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/2/like/1");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/2/like/2");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.get(url + "/popular");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("film")))
                .andExpect(jsonPath("$[1].name", is("new film")));
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

    private @NotNull MockHttpServletRequestBuilder postRequest(User user) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(user));
    }
}
