package com.bookify.user_service;

import ch.qos.logback.core.model.Model;
import com.bookify.user_service.dto.BookDTO;
import com.bookify.user_service.dto.RentalDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;


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
    @Operation(summary = "Creates new user", description = "Used to create new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public User createUser(@RequestBody @Valid User user) { // @Valid eklendi
        return userRepository.save(user);
    }

    @GetMapping
    @Operation(summary = "Lists all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users successfully listed",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) })
    })
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}/rentals")
    @Operation(summary = "Lists a user's rentals", description = "Lists a specific user's rentals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's rentals successfully listed.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    public List<BookDTO> getRentedBooksForUser(@PathVariable Long userId) {
        try {
            List<RentalDTO> userRentals = webClientBuilder.build()
                    .get()
                    .uri("http://rental-service/api/rentals/user/" + userId)
                    .retrieve()
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
                                    .uri("http://book-service/api/books/" + rental.getBookId())
                                    .retrieve()
                                    .bodyToMono(BookDTO.class)
                                    .block()
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

}

