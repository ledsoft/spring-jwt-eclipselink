package com.github.ledsoft.demo.service;

import com.github.ledsoft.demo.SpringJwtEclipselinkDemo;
import com.github.ledsoft.demo.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackageClasses = SpringJwtEclipselinkDemo.class)
class UserServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService sut;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createEncodesPassword() {
        final User user = new User();
        user.setFirstName("Catherine");
        user.setLastName("Halsey");
        user.setUsername("halsey@unsc.org");
        final String plainPassword = "117";
        user.setPassword(plainPassword);
        sut.create(user);

        final User result = em.find(User.class, user.getId());
        assertNotNull(result);
        assertTrue(passwordEncoder.matches(plainPassword, result.getPassword()));
    }
}