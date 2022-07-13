package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test_data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
public class FilmRatingControllerTest {

    private final String url = "/mpa";
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRatingById() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(url + "/1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("G")));
    }

    @Test
    public void getAllRatings() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[3].name", is("R")));
    }

    @Test
    public void getUnknownRating() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(url + "/15");
        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }
}
