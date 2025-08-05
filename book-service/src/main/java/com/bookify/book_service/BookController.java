package com.bookify.book_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    @PutMapping("/{id}/rent")
    @ResponseStatus(HttpStatus.OK)
    public void markAsRented(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (book.isAvailable()) {
            book.setAvailable(false);
            bookRepository.save(book);
        } else {
            throw new RuntimeException("This book has already been rented.");
        }
    }

    @PutMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    public void markAsReturned(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (!book.isAvailable()) {
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
}