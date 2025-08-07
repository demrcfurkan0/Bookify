package com.bookify.user_service;

import com.bookify.user_service.dto.BookDTO;
import com.bookify.user_service.dto.RentalDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class UserUIController {

    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder; // YENİ

    public UserUIController(UserRepository userRepository, WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/ui/users")
    public String showUsersPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("userList", users);
        return "users";
    }

    @GetMapping("/ui/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/ui/users/add")
    public String addUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/ui/users";
    }

    @PostMapping("/ui/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/ui/users";
    }

    @GetMapping("/ui/users/{userId}/rentals")
    public String showUserRentalsPage(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            List<RentalDTO> userRentals = webClientBuilder.build().get()
                    .uri("http://rental-service/api/rentals/user/" + userId)
                    .retrieve()
                    .bodyToFlux(RentalDTO.class)
                    .collectList()
                    .block();

            List<BookDTO> rentedBooks;
            if (userRentals == null || userRentals.isEmpty()) {
                rentedBooks = Collections.emptyList();
            } else {
                rentedBooks = userRentals.stream()
                        .map(rental -> webClientBuilder.build().get()
                                .uri("http://book-service/api/books/" + rental.getBookId())
                                .retrieve()
                                .bodyToMono(BookDTO.class)
                                .block())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

            model.addAttribute("user", user);
            model.addAttribute("rentedBooks", rentedBooks);

        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("rentedBooks", Collections.emptyList());

        }

        return "user-rentals";
    }
}