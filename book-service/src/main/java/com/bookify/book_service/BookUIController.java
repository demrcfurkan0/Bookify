package com.bookify.book_service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
public class BookUIController {

    private final BookRepository bookRepository;

    public BookUIController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/ui/books")
    public String showBooksPage(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("bookList", books);
        return "books";
    }

    @GetMapping("/ui/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/ui/books/add")
    public String addBook(@ModelAttribute Book book) {
        book.setAvailable(true);
        bookRepository.save(book);
        return "redirect:/ui/books";
    }
}