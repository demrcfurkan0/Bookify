package com.bookify.rental_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDTO {
    private Long id;
    private String bookName;
    private boolean isAvailable;
}