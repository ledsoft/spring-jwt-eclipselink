package com.github.ledsoft.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ledsoft.demo.environment.Environment;
import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.security.model.LoginStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("security")
class AuthenticationFailureTest {

    private AuthenticationFailure failure;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = Environment.objectMapper();
        this.failure = new AuthenticationFailure(mapper);
    }

    @Test
    void authenticationFailureReturnsLoginStatusWithErrorInfoOnUsernameNotFound() throws Exception {
        final MockHttpServletRequest request = Generator.mockRequest();
        final MockHttpServletResponse response = Generator.mockResponse();
        final String msg = "Username not found";
        final AuthenticationException e = new UsernameNotFoundException(msg);
        failure.onAuthenticationFailure(request, response, e);
        final LoginStatus status = mapper.readValue(response.getContentAsString(), LoginStatus.class);
        assertFalse(status.isSuccess());
        assertFalse(status.isLoggedIn());
        assertNull(status.getUsername());
        assertEquals(msg, status.getErrorMessage());
        assertEquals("login.error", status.getErrorId());
    }

    @Test
    void authenticationFailureReturnsLoginStatusWithErrorInfoOnAccountLocked() throws Exception {
        final MockHttpServletRequest request = Generator.mockRequest();
        final MockHttpServletResponse response = Generator.mockResponse();
        final String msg = "Account is locked.";
        failure.onAuthenticationFailure(request, response, new LockedException(msg));
        final LoginStatus status = mapper.readValue(response.getContentAsString(), LoginStatus.class);
        assertFalse(status.isSuccess());
        assertFalse(status.isLoggedIn());
        assertNull(status.getUsername());
        assertEquals(msg, status.getErrorMessage());
        assertEquals("login.locked", status.getErrorId());
    }

    @Test
    void authenticationFailureReturnsLoginStatusWithErrorInfoOnAccountDisabled() throws Exception {
        final MockHttpServletRequest request = Generator.mockRequest();
        final MockHttpServletResponse response = Generator.mockResponse();
        final String msg = "Account is disabled.";
        failure.onAuthenticationFailure(request, response, new DisabledException(msg));
        final LoginStatus status = mapper.readValue(response.getContentAsString(), LoginStatus.class);
        assertFalse(status.isSuccess());
        assertFalse(status.isLoggedIn());
        assertNull(status.getUsername());
        assertEquals(msg, status.getErrorMessage());
        assertEquals("login.disabled", status.getErrorId());
    }
}