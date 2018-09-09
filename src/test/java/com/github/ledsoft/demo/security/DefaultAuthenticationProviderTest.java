package com.github.ledsoft.demo.security;

import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.AuthenticationToken;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import com.github.ledsoft.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class DefaultAuthenticationProviderTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DefaultAuthenticationProvider sut;

    @Test
    void authenticateReturnsAuthenticatedUserToken() {
        final User user = Generator.generateUser();
        final String password = user.getPassword();
        userService.create(user);
        final Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
        final AuthenticationToken result = sut.authenticate(auth);
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        final DemoUserDetails userDetails = result.getDetails();
        assertEquals(user, userDetails.getUser());
    }

    @Test
    void authenticateSetsAuthenticatedUserSecurityContext() {
        final User user = Generator.generateUser();
        final String password = user.getPassword();
        userService.create(user);
        final Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
        sut.authenticate(auth);
        final User result = SecurityUtils.getCurrentUser();
        assertEquals(user, result);
    }

    @Test
    void authenticateThrowsBadCredentialsExceptionWhenInvalidPasswordIsSupplied() {
        final User user = Generator.generateUser();
        userService.create(user);
        final Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), "invalidPassword");
        assertThrows(BadCredentialsException.class, () -> sut.authenticate(auth));
    }
}