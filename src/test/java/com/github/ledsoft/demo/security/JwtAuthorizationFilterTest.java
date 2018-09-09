package com.github.ledsoft.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ledsoft.demo.environment.Environment;
import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.rest.model.ErrorInfo;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import com.github.ledsoft.demo.service.security.AppUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.FilterChain;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("security")
class JwtAuthorizationFilterTest {

    private User user;

    private MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    @Mock
    private FilterChain chainMock;

    @Mock
    private AuthenticationManager authManagerMock;

    @Mock
    private AppUserDetailsService detailsServiceMock;

    private ObjectMapper objectMapper;

    private JwtUtils jwtUtilsSpy;

    private JwtAuthorizationFilter sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.user = Generator.generateUser();
        user.setId(Generator.randomInt());
        this.objectMapper = Environment.objectMapper();
        this.jwtUtilsSpy = spy(new JwtUtils());
        this.sut = new JwtAuthorizationFilter(authManagerMock, jwtUtilsSpy, objectMapper, detailsServiceMock);
        when(detailsServiceMock.loadUserByUsername(user.getUsername())).thenReturn(new DemoUserDetails(user));
        resetSecurityContext();
    }

    private static void resetSecurityContext() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    void doFilterInternalExtractsUserInfoFromJwtAndSetsUpSecurityContext() throws Exception {
        generateJwtIntoRequest();

        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        final User result = SecurityUtils.getCurrentUser();
        assertEquals(user, result);
    }

    private void generateJwtIntoRequest() {
        final String token = generateJwt();
        mockRequest.addHeader(SecurityConstants.AUTHENTICATION_HEADER, SecurityConstants.JWT_TOKEN_PREFIX + token);
    }

    private String generateJwt() {
        return Jwts.builder().setSubject(user.getUsername())
                   .setId(user.getId().toString())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 10000))
                   .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
    }

    @Test
    void doFilterInternalInvokesFilterChainAfterSuccessfulExtractionOfUserInfo() throws Exception {
        generateJwtIntoRequest();
        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        verify(chainMock).doFilter(mockRequest, mockResponse);
    }

    @Test
    void doFilterInternalLeavesEmptySecurityContextAndPassesRequestDownChainWhenAuthenticationIsMissing()
            throws Exception {
        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        verify(chainMock).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalLeavesEmptySecurityContextAndPassesRequestDownChainWhenAuthenticationHasIncorrectFormat()
            throws Exception {
        mockRequest.addHeader(SecurityConstants.AUTHENTICATION_HEADER, generateJwt());
        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        verify(chainMock).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalRefreshesUserTokenOnSuccessfulAuthorization() throws Exception {
        generateJwtIntoRequest();
        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        assertTrue(mockResponse.containsHeader(SecurityConstants.AUTHENTICATION_HEADER));
        assertNotEquals(mockRequest.getHeader(SecurityConstants.AUTHENTICATION_HEADER),
                mockResponse.getHeader(SecurityConstants.AUTHENTICATION_HEADER));
        verify(jwtUtilsSpy).refreshToken(any());
    }

    @Test
    void doFilterInternalWritesErrorInfoDirectlyIntoResponseOnExpiredToken() throws Exception {
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setId(user.getId().toString())
                                 .setIssuedAt(new Date())
                                 .setExpiration(new Date(System.currentTimeMillis() - 10000))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        mockRequest.addHeader(SecurityConstants.AUTHENTICATION_HEADER, SecurityConstants.JWT_TOKEN_PREFIX + token);
        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        verify(chainMock, never()).doFilter(mockRequest, mockResponse);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), mockResponse.getStatus());
        final ErrorInfo errorInfo = objectMapper.readValue(mockResponse.getContentAsString(), ErrorInfo.class);
        assertThat(errorInfo.getMessage(), containsString("expired"));
    }

    @Test
    void doFilterInternalWritesErrorInfoDirectlyIntoResponseOnIncompleteToken() throws Exception {
        // Missing id in token
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setIssuedAt(new Date())
                                 .setExpiration(new Date(System.currentTimeMillis() + 10000))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        mockRequest.addHeader(SecurityConstants.AUTHENTICATION_HEADER, SecurityConstants.JWT_TOKEN_PREFIX + token);
        sut.doFilterInternal(mockRequest, mockResponse, chainMock);
        verify(chainMock, never()).doFilter(mockRequest, mockResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), mockResponse.getStatus());
        final ErrorInfo errorInfo = objectMapper.readValue(mockResponse.getContentAsString(), ErrorInfo.class);
        assertThat(errorInfo.getMessage(), containsString("missing id"));
    }
}