package com.bookify.book_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Test
    public void whenGetAllBooks_thenReturnJsonArray() throws Exception {
        Book book1 = new Book(1L, "Kitap 1", "Yazar 1", "111", true);
        Book book2 = new Book(2L, "Kitap 2", "Yazar 2", "222", true);
        List<Book> allBooks = Arrays.asList(book1, book2);

        given(bookRepository.findAll()).willReturn(allBooks);

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookName", is("Kitap 1")))
                .andExpect(jsonPath("$[1].bookName", is("Kitap 2")));
    }

    @Test
    public void whenGetBookById_thenReturnBook() throws Exception {
        Book book = new Book(1L, "Test Kitabı", "Test Yazar", "123", true);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName", is("Test Kitabı")))
                .andExpect(jsonPath("$.writer", is("Test Yazar")));
    }
}