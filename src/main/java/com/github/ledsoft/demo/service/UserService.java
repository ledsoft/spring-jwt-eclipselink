package com.github.ledsoft.demo.service;

import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void create(User user) {
        Objects.requireNonNull(user);
        user.encodePassword(passwordEncoder);
        repository.save(user);
    }
}
