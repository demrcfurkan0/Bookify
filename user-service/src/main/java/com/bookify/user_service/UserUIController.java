package com.bookify.user_service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
public class UserUIController {

    private final UserRepository userRepository;

    // Constructor Injection
    public UserUIController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/ui/users")
    public String showUsersPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("userList", users);
        return "users";
    }
    @PostMapping("/ui/users/add")
    public String addUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/ui/users";
    }
    @GetMapping("/ui/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }
}

