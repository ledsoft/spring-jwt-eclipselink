package com.github.ledsoft.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ledsoft.demo.exception.JwtException;
import com.github.ledsoft.demo.exception.TokenExpiredException;
import com.github.ledsoft.demo.rest.model.ErrorInfo;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import com.github.ledsoft.demo.service.security.AppUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter retrieves JWT from the incoming request and validates it, ensuring that the user is authorized to access
 * the application.
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final AppUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                                  ObjectMapper objectMapper,
                                  AppUserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final String authHeader = request.getHeader(SecurityConstants.AUTHENTICATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        final String authToken = authHeader.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
        try {
            final DemoUserDetails userDetails = jwtUtils.extractUserInfo(authToken);
            final DemoUserDetails existingDetails = userDetailsService.loadUserByUsername(userDetails.getUsername());
            SecurityUtils.setCurrentUser(existingDetails);
            refreshToken(authToken, response);
        } catch (TokenExpiredException e) {
            // It is important to call set status before writing into response body
            // Once one writes into response body, it becomes committed and status cannot be set
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getOutputStream(), new ErrorInfo(request.getRequestURI(), e.getMessage()));
            return;
        } catch (JwtException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            objectMapper.writeValue(response.getOutputStream(), new ErrorInfo(request.getRequestURI(), e.getMessage()));
            return;
        }

        chain.doFilter(request, response);
    }

    private void refreshToken(String authToken, HttpServletResponse response) {
        final String newToken = jwtUtils.refreshToken(authToken);
        response.setHeader(SecurityConstants.AUTHENTICATION_HEADER, SecurityConstants.JWT_TOKEN_PREFIX + newToken);
    }
}
