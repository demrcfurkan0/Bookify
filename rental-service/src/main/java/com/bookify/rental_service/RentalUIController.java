package com.bookify.rental_service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class RentalUIController {

    private final RentalRepository rentalRepository;

    public RentalUIController(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/ui/rentals")
    public String showRentalsPage(Model model) {
        List<Rental> rentals = rentalRepository.findAll();
        model.addAttribute("rentalList", rentals);
        return "rentals";
    }
}