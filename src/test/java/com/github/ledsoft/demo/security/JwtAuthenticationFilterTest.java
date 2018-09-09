package com.github.ledsoft.demo.security;

import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.AuthenticationToken;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.FilterChain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Tag("security")
class JwtAuthenticationFilterTest {

    private MockHttpServletRequest mockRequest;

    private MockHttpServletResponse mockResponse;

    private User user;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.user = Generator.generateUser();
        user.setId(Generator.randomInt());
        this.mockRequest = new MockHttpServletRequest();
        this.mockResponse = new MockHttpServletResponse();
        this.sut = new JwtAuthenticationFilter(mock(AuthenticationManager.class), new JwtUtils());
    }

    @Test
    void successfulAuthenticationExposesAuthenticationHeaderToClient() throws Exception {
        final AuthenticationToken token = new AuthenticationToken(new DemoUserDetails(user));
        sut.successfulAuthentication(mockRequest, mockResponse, filterChain, token);
        assertTrue(mockResponse.containsHeader(SecurityConstants.AUTHENTICATION_HEADER));
        final String value = mockResponse.getHeader(SecurityConstants.EXPOSE_HEADERS_HEADER);
        assertNotNull(value);
        assertThat(value, containsString(SecurityConstants.AUTHENTICATION_HEADER));
    }

    @Test
    void successfulAuthenticationAddsJWTToResponse() throws Exception {
        final AuthenticationToken token = new AuthenticationToken(new DemoUserDetails(user));
        sut.successfulAuthentication(mockRequest, mockResponse, filterChain, token);
        assertTrue(mockResponse.containsHeader(SecurityConstants.AUTHENTICATION_HEADER));
        final String value = mockResponse.getHeader(SecurityConstants.AUTHENTICATION_HEADER);
        assertNotNull(value);
        assertTrue(value.startsWith(SecurityConstants.JWT_TOKEN_PREFIX));
        final String jwtToken = value.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
        final Jws<Claims> jwt = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(jwtToken);
        assertFalse(jwt.getBody().isEmpty());
    }
}