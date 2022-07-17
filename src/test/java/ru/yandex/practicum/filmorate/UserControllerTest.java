package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test_data.sql")
@Sql(scripts = "/delete-data.sql", executionPhase = AFTER_TEST_METHOD)
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
        mockMvc.perform(mockRequest).andExpect(status().isNotFound());

        mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("zxcv@mail.com");
        user2.setLogin("asdf");
        user2.setName("Name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        mockRequest = putRequest(user2);
        mockMvc.perform(mockRequest).andExpect(status().isNotFound());
    }

    @Test
    public void getUserById() throws Exception {
        User user = new User();
        user.setEmail("qwer@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.get(url + "/1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nick")));
    }

    @Test
    public void addFriend() throws Exception {
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

        mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/friends/2");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        mockRequest = MockMvcRequestBuilders.get(url + "/1/friends");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));

        mockRequest = MockMvcRequestBuilders.get(url + "/2/friends");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void removeFriend() throws Exception {
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

        mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/friends/2");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.delete(url + "/1/friends/2");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        mockRequest = MockMvcRequestBuilders.get(url + "/1/friends");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockRequest = MockMvcRequestBuilders.get(url + "/2/friends");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getFriends() throws Exception {
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

        mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/friends/2");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.get(url + "/1/friends");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    public void getCommonFriends() throws Exception {
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

        mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user3 = new User();
        user3.setEmail("zxcv@mail.com");
        user3.setLogin("zxcvb");
        user3.setName("New name");
        user3.setBirthday(LocalDate.of(2010, 1, 1));

        mockRequest = postRequest(user3);
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/friends/2");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/2/friends/1");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/1/friends/3");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.put(url + "/3/friends/1");
        mockMvc.perform(mockRequest);

        mockRequest = MockMvcRequestBuilders.get(url + "/2/friends/common/3");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
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
