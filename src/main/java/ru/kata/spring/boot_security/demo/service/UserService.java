package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void addUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresentOrElse((r) -> {}, () -> {
            makeUserIfNot(user);
            createUser(user);
        });
    }

    @Transactional
    public void updateUser(User newUser) {
        User oldUser = userRepository.findById(newUser.getId()).orElseThrow(RuntimeException::new);
        oldUser.setUsername(newUser.getUsername());
        oldUser.setAge(newUser.getAge());
        oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        oldUser.setRoles(newUser.getRoles());
        makeUserIfNot(oldUser);
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.findByUsername(username).ifPresent((this::flushUser));
    }

    @PostConstruct
    public void addRoles() {
        Role user = roleService.getRole("ROLE_USER");
        Role admin = roleService.getRole("ROLE_ADMIN");
        userRepository.save(new User("Admin", passwordEncoder.encode("123"), 13, List.of(user, admin)));
        userRepository.save(new User("Alena", passwordEncoder.encode("123"), 19, List.of(user, admin)));
        userRepository.save(new User("User2", passwordEncoder.encode("123"), 19, List.of(user)));
    }


    private void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private void flushUser(User user) {
        user.setRoles(null);
        userRepository.delete(user);
    }

    private void makeUserIfNot(User oldUser) {
        switch (oldUser.getRoles().size()) {
            case 0:
                oldUser.addRole(roleService.getRole("ROLE_USER"));
            case 1:
                if (!oldUser.getRoles().get(0).toString().equals("USER")) {
                    oldUser.addRole(roleService.getRole("ROLE_USER"));
                }
            case 2:
                break;
            default:
                throw new RuntimeException("User with id=" + oldUser.getId() + " has invalid count of roles");
        }
    }
}
