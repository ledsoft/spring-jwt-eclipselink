package com.github.ledsoft.demo.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final DemoUserDetails userDetails;

    public AuthenticationToken(DemoUserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return userDetails.getPassword();
    }

    @Override
    public DemoUserDetails getPrincipal() {
        return userDetails;
    }

    @Override
    public DemoUserDetails getDetails() {
        return userDetails;
    }
}
