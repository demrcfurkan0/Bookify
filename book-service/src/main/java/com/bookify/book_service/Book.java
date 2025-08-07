package com.bookify.book_service;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
@Schema(description = "Book Model")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Book ID", example = "1")
    private Long id;

    @Column(name = "book_name")
    @NotEmpty(message = "The book title cannot be blank.")
    @Schema(description = "Book Name", example = "Harry Potter")
    private String bookName;

    @Column(name = "writer")
    @NotEmpty(message = "Writer name cannot be empty")
    @Schema(description = "Writer", example = "Asdasd")
    private String writer;

    @Column(name = "isbn")
    @NotEmpty(message = "ISBN no cannot be empty")
    @Schema(description = "ISBN no", example = "978-1234567890")
    private String isbn;

    @Column(name = "is_available")
    @Schema(description = "Book isAvailable?", example = "true")
    private boolean isAvailable = true;

}