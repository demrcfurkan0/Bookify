package com.bookify.rental_service;

import com.bookify.rental_service.RentalController;
import com.bookify.rental_service.RentalRepository;
import com.bookify.rental_service.dto.BookDTO;
import com.bookify.rental_service.dto.RentalRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RentalController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalRepository rentalRepository;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    public void whenRentBook_andBookIsAvailable_thenReturnSuccessMessage() throws Exception {
        // 1. Hazırlık (Arrange)
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setBookId(1L);
        rentalRequest.setUserId(1L);

        BookDTO availableBook = new BookDTO();
        availableBook.setId(1L);
        availableBook.setBookName("Test Kitap");
        availableBook.setAvailable(true);

        // WebClient GET çağrısını mock'lama
        when(webClient
                .get()
                .uri("http://book-service/api/books/" + rentalRequest.getBookId())
                .retrieve()
                .bodyToMono(BookDTO.class))
                .thenReturn(Mono.just(availableBook));

        // WebClient PUT çağrısını mock'lama
        when(webClient
                .put()
                .uri("http://book-service/api/books/" + rentalRequest.getBookId() + "/rent")
                .retrieve()
                .bodyToMono(Void.class))
                .thenReturn(Mono.empty());

        // 2. Eylem (Act) & 3. Doğrulama (Assert)
        mockMvc.perform(post("/api/rentals/rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Book rented successfully!"));
    }
}