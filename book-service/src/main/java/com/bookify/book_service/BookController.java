package com.bookify.book_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adds new book", description = "Adding new book to db.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book added successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public Book createBook(@RequestBody @Valid Book book) {
        return bookRepository.save(book);
    }

    @GetMapping
    @Operation(summary = "List all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books listed successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) })
    })
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Bring book info to id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " Book found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "404", description = "Book cannot founc", content = @Content)
    })
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: " + id));
    }

    @PutMapping("/{id}/rent")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Rents Book")
    public void markAsRented(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: " + id));

        if (book.isAvailable()) {
            book.setAvailable(false);
            bookRepository.save(book);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This book has already been rented.");
        }
    }

    @PutMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "ReturnsBook")
    public void markAsReturned(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: " + id));

        if (!book.isAvailable()) {
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
}