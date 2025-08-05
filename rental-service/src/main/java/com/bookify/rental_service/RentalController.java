package com.bookify.rental_service;

import com.bookify.rental_service.dto.BookDTO;
import com.bookify.rental_service.dto.RentalRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalRepository rentalRepository;
    private final WebClient.Builder webClientBuilder;

    // Constructor Injection
    public RentalController(RentalRepository rentalRepository, WebClient.Builder webClientBuilder) {
        this.rentalRepository = rentalRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping("/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public String rentBook(@RequestBody RentalRequest rentalRequest) {
        BookDTO book = webClientBuilder.build()
                .get()
                .uri("http://book-service/api/books/" + rentalRequest.getBookId())                 .retrieve()
                .bodyToMono(BookDTO.class)
                .block();

        if (book == null || !book.isAvailable()) {
            return "This book is not available or could not be found.";
        }

        webClientBuilder.build()
                .put()
                .uri("http://book-service/api/books/" + rentalRequest.getBookId() + "/rent")                .retrieve()
                .bodyToMono(Void.class)
                .block();

        Rental newRental = new Rental();
        newRental.setBookId(rentalRequest.getBookId());
        newRental.setUserId(rentalRequest.getUserId());
        newRental.setRentalDate(LocalDate.now());

        rentalRepository.save(newRental);

        return "Book rented successfully!";
    }

    @PutMapping("/{rentalId}/return")
    @ResponseStatus(HttpStatus.OK)
    public String returnBook(@PathVariable Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental record not found with id: " + rentalId));

        webClientBuilder.build()
                .put()
                .uri("http://book-service/api/books/" + rental.getBookId() + "/return")
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        rental.setReturnDate(LocalDate.now());
        rentalRepository.save(rental);

        return "Book returned successfully!";
    }

    @GetMapping("/user/{userId}")
    public List<Rental> getRentalsByUserId(@PathVariable Long userId) {
        return rentalRepository.findByUserId(userId);
    }
}