package com.github.ledsoft.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ledsoft.demo.environment.Environment;
import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.AuthenticationToken;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import com.github.ledsoft.demo.security.model.LoginStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

@Tag("security")
class AuthenticationSuccessTest {

    private User person = Generator.generateUser();

    private AuthenticationSuccess success;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = Environment.objectMapper();
        this.success = new AuthenticationSuccess(mapper);
    }

    @Test
    void authenticationSuccessReturnsResponseContainingUsername() throws Exception {
        final MockHttpServletResponse response = Generator.mockResponse();
        success.onAuthenticationSuccess(Generator.mockRequest(), response, generateAuthenticationToken());
        verifyLoginStatus(response);
    }

    private void verifyLoginStatus(MockHttpServletResponse response) throws java.io.IOException {
        final LoginStatus status = mapper.readValue(response.getContentAsString(), LoginStatus.class);
        assertTrue(status.isSuccess());
        assertTrue(status.isLoggedIn());
        assertEquals(person.getUsername(), status.getUsername());
        assertNull(status.getErrorMessage());
    }

    private Authentication generateAuthenticationToken() {
        final DemoUserDetails userDetails = new DemoUserDetails(person);
        return new AuthenticationToken(userDetails);
    }

    @Test
    void logoutSuccessReturnsResponseContainingLoginStatus() throws Exception {
        final MockHttpServletResponse response = Generator.mockResponse();
        success.onLogoutSuccess(Generator.mockRequest(), response, generateAuthenticationToken());
        final LoginStatus status = mapper.readValue(response.getContentAsString(), LoginStatus.class);
        assertTrue(status.isSuccess());
        assertFalse(status.isLoggedIn());
        assertNull(status.getUsername());
        assertNull(status.getErrorMessage());
    }
}