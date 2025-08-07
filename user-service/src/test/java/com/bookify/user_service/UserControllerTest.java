package com.bookify.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    public void whenGetAllUsers_thenReturnJsonArray() throws Exception {

        User user1 = new User(1L, "Ahmet", "Yılmaz", "ahmet@example.com");
        User user2 = new User(2L, "Zeynep", "Kaya", "zeynep@example.com");
        List<User> allUsers = Arrays.asList(user1, user2);

        given(userRepository.findAll()).willReturn(allUsers);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Ahmet")))
                .andExpect(jsonPath("$[1].firstName", is("Zeynep")));
    }

    @Test
    public void whenCreateUser_thenReturnCreatedUser() throws Exception {
        User userToSave = new User(null, "Mehmet", "Öztürk", "mehmet@example.com");
        User savedUser = new User(1L, "Mehmet", "Öztürk", "mehmet@example.com");

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Mehmet")));
    }
}