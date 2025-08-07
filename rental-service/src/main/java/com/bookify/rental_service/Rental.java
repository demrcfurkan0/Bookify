package com.bookify.rental_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rentals")
@Schema(description = "Rental Model")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Rent ID", example = "1")
    private Long id;

    @Column(name = "book_id")
    @Schema(description = "Book ID", example = "1")
    private Long bookId;

    @Column(name = "user_id")
    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Column(name = "rental_date")
    @Schema(description = "Rental Date", example = "06.08.2025")
    private LocalDate rentalDate;

    @Column(name = "return_date")
    @Schema(description = "Return Date", example = "06.08.2025")
    private LocalDate returnDate;
}