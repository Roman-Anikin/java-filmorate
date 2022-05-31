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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String url = "/users";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getUsers() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("Nick")));
    }

    @Test
    public void addValidUser() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("qwer@mail.com")))
                .andExpect(jsonPath("$.login", is("qwerty")))
                .andExpect(jsonPath("$.name", is("Nick")))
                .andExpect(jsonPath("$.birthday", is("2000-01-01")));
    }

    @Test
    public void updateUser() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("asdf@mail.com");
        user2.setLogin("asdfg");
        user2.setName("Name");
        user2.setBirthday(LocalDate.of(2000, 10, 10));

        mockRequest = putRequest(user2);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Name")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("asdf@mail.com")));
    }

    @Test
    public void addUserWithWrongLogin() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwe rty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithWrongEmail() throws Exception {
        User user = new User();
        user.setEmail("mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithBirthdayInFuture() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2030, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyName() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getLogin())));
    }

    @Test
    public void wrongUserUpdate() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = putRequest(user);
        mockMvc.perform(mockRequest).andExpect(status().isInternalServerError());

        mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("zxcv@mail.com");
        user2.setLogin("asdf");
        user2.setName("Name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        mockRequest = putRequest(user2);
        mockMvc.perform(mockRequest).andExpect(status().isInternalServerError());
    }

    private @NotNull MockHttpServletRequestBuilder postRequest(User user) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(user));
    }

    private @NotNull MockHttpServletRequestBuilder putRequest(User user) throws JsonProcessingException {
        return MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(user));
    }
}
