package com.github.ledsoft.demo.security.model;

import com.github.ledsoft.demo.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DemoUserDetails implements UserDetails {

    public static final String DEFAULT_ROLE = "ROLE_USER";

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public DemoUserDetails(User user) {
        this.user = user;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(DEFAULT_ROLE));
    }

    public DemoUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = new ArrayList<>(authorities);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void eraseCredentials() {
        user.eraseCredentials();
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "user=" + user +
                ", authorities=" + authorities +
                '}';
    }
}
