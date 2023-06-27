package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;


@Validated
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String displayUsers(Model model, Principal principal) {
        model.addAttribute("allUsers", userService.findAll());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
        return "users";
    }

    @GetMapping("/{username}")
    public String displayUser(@PathVariable("username") String username, Model model, Principal principal) {
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
        return "singleUser";
    }

    //Create
    @GetMapping("/addNew")
    public String addNewUser(Model model) {
        model.addAttribute("user", new User());
        return "addNew";
    }

    @PostMapping("/addNew")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "addNew";
        } else {
            userService.addUser(user);
            return "redirect:/admin";
        }
    }

    //Update
    @GetMapping("/{username}/update")
    public String updateUserForm(@PathVariable("username") String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        return "update";
    }


    @PatchMapping("/{username}")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update";
        } else {
            userService.updateUser(user);
            return "redirect:/admin";
        }
    }

    @PatchMapping("/{username}/makeAdmin")
    public String makeUserAdmin(@PathVariable("username") String username) {
        userService.makeUserAdmin(userService.findByUsername(username));
        return "redirect:/admin";
    }

    @PatchMapping("/{username}/unmakeAdmin")
    public String unmakeUserAdmin(@PathVariable("username") String username) {
        userService.unmakeUserAdmin(userService.findByUsername(username));
        return "redirect:/admin";
    }

    //Delete
    @DeleteMapping("/{username}")
    public String deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(userService.findByUsername(username));
        return "redirect:/admin";
    }
}
