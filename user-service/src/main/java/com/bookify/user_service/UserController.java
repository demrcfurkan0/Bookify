package com.bookify.user_service;


import com.bookify.user_service.dto.BookDTO;
import com.bookify.user_service.dto.RentalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;

    public UserController(UserRepository userRepository, WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}/rentals")
    public List<BookDTO> getRentedBooksForUser(@PathVariable Long userId) {
        List<RentalDTO> userRentals = webClientBuilder.build()
                .get()
                .uri("http://rental-service/api/rentals/user/" + userId)                .retrieve()
                .bodyToFlux(RentalDTO.class)
                .collectList()
                .block();

        if (userRentals == null || userRentals.isEmpty()) {
            return Collections.emptyList();
        }

        return userRentals.stream()
                .map(rental ->
                        webClientBuilder.build()
                                .get()
                                .uri("http://book-service/api/books/" + rental.getBookId())                                .retrieve()
                                .bodyToMono(BookDTO.class)
                                .block()
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}