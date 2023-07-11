package ru.kata.spring.boot_security.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UsersAndRoles implements CommandLineRunner {
    private RoleService roleService;
    private UserService userService;

    @Autowired
    public UsersAndRoles(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        Role userTest = new Role(1L, "ROLE_USER");
        Role adminTest = new Role(2L, "ROLE_ADMIN");
        roleService.saveRole(userTest);
        roleService.saveRole(adminTest);
        Set<Role> userSet = Stream.of(userTest).collect(Collectors.toSet());
        Set<Role> adminSet = Stream.of(adminTest).collect(Collectors.toSet());

        User user = new User("User", "User", "user", "user", userSet);
        User admin = new User("Admin", "Admin", "admin", "admin", adminSet);
        userService.add(user);
        userService.add(admin);

    }
}
