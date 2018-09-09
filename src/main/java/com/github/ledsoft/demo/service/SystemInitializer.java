package com.github.ledsoft.demo.service;

import com.github.ledsoft.demo.model.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SystemInitializer implements ApplicationRunner {

    private final UserService userService;

    public SystemInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        final User lasky = new User("Thomas", "Lasky", "lasky@unsc.org");
        lasky.setPassword("infinity");
        final User palmer = new User("Sarah", "Palmer", "palmer@usc.org");
        palmer.setPassword("spartans");
        userService.create(lasky);
        userService.create(palmer);
    }
}
