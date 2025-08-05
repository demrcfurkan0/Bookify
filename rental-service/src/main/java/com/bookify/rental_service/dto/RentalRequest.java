package com.bookify.rental_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RentalRequest {
    private Long bookId;
    private Long userId;
}