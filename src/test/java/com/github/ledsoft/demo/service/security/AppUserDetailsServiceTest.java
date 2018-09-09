package com.github.ledsoft.demo.service.security;

import com.github.ledsoft.demo.SpringJwtEclipselinkDemo;
import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackageClasses = SpringJwtEclipselinkDemo.class)
class AppUserDetailsServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AppUserDetailsService sut;

    @Test
    void loadByUsernameReturnsUserDetailsWithUser() {
        final User user = Generator.generateUser();
        em.persistAndFlush(user);

        final DemoUserDetails result = sut.loadUserByUsername(user.getUsername());
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void loadByUsernameThrowsUsernameNotFoundForUnknownUsername() {
        assertThrows(UsernameNotFoundException.class, () -> sut.loadUserByUsername("uknown.user@timesheeter.org"));
    }
}