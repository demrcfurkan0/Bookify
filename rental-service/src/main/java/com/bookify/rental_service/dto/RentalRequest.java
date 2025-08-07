package com.bookify.rental_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Data
@Getter
@Setter
@NoArgsConstructor
public class RentalRequest {
    @NotNull(message = "Book ID cannot be null")
    private Long bookId;
    @NotNull(message = "User ID cannot be null")
    private Long userId;
}