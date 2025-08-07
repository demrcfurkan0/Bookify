package com.bookify.rental_service;

import com.bookify.rental_service.dto.BookDTO;
import com.bookify.rental_service.dto.RentalRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalRepository rentalRepository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger log = LoggerFactory.getLogger(RentalController.class);


    public RentalController(RentalRepository rentalRepository, WebClient.Builder webClientBuilder) {
        this.rentalRepository = rentalRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping("/rent")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Rent Book", description = "Creates a new rental record for the specified book and user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book successfully rented."),
            @ApiResponse(responseCode = "400", description = "Book not found or unavailable", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    public String rentBook(@RequestBody @Valid RentalRequest rentalRequest) {
        try {
            BookDTO book = webClientBuilder.build()
                    .get()
                    .uri("http://book-service/api/books/" + rentalRequest.getBookId())
                    .retrieve()
                    .bodyToMono(BookDTO.class)
                    .block();
            log.info("Received response from book-service: {}", book);

            if (book == null || !book.isAvailable()) {
                log.error("Book is not available or not found. Book details: {}", book);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This book is not available or could not be found.");
            }

            webClientBuilder.build()
                    .put()
                    .uri("http://book-service/api/books/" + rentalRequest.getBookId() + "/rent")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            Rental newRental = new Rental();
            newRental.setBookId(rentalRequest.getBookId());
            newRental.setUserId(rentalRequest.getUserId());
            newRental.setRentalDate(LocalDate.now());

            rentalRepository.save(newRental);

            return "Book rented successfully!";
        } catch (Exception e) {
            log.error("Error during rental process for book ID: {}", rentalRequest.getBookId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", e);
        }
    }

    @PutMapping("/{rentalId}/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return the book", description = "Returns the specified rental record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully returned."),
            @ApiResponse(responseCode = "404", description = "No rental record found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası", content = @Content)
    })
    public String returnBook(@PathVariable Long rentalId) {
        try {
            Rental rental = rentalRepository.findById(rentalId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental record not found with id: " + rentalId));

            webClientBuilder.build()
                    .put()
                    .uri("http://book-service/api/books/" + rental.getBookId() + "/return")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            rental.setReturnDate(LocalDate.now());
            rentalRepository.save(rental);

            return "Book returned successfully!";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lists rentals to a user", description = "Returns the rental records specified user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User rentals successfully listed."),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    public List<Rental> getRentalsByUserId(@PathVariable Long userId) {
        return rentalRepository.findByUserId(userId);
    }
}