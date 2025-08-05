package com.bookify.user_service.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
public class RentalDTO {
    private Long bookId;
    private LocalDate rentalDate;
}