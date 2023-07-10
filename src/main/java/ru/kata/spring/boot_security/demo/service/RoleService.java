package ru.kata.spring.boot_security.demo.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Role getRole(String role) {
        return roleRepository.findByRole(role)
                .orElseThrow(IllegalArgumentException::new);
    }

    @PostConstruct
    public void addRoles() {
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_ADMIN"));
    }

    @Transactional
    public Collection<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role getById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
}
