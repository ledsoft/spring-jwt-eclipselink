package com.github.ledsoft.demo.security;

import com.github.ledsoft.demo.environment.Generator;
import com.github.ledsoft.demo.exception.IncompleteJwtException;
import com.github.ledsoft.demo.exception.JwtException;
import com.github.ledsoft.demo.exception.TokenExpiredException;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final List<String> ROLES = Arrays.asList("USER", "ADMIN");

    private User user;

    private JwtUtils sut;

    @BeforeEach
    void setUp() {
        this.user = Generator.generateUser();
        user.setId(Generator.randomInt());
        this.sut = new JwtUtils();
    }

    @Test
    void generateTokenCreatesJwtForUserWithoutAuthorities() {
        final DemoUserDetails userDetails = new DemoUserDetails(user);
        final String jwtToken = sut.generateToken(userDetails);
        verifyJWToken(jwtToken, userDetails);
    }

    private void verifyJWToken(String token, DemoUserDetails userDetails) {
        final Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token).getBody();
        assertEquals(user.getUsername(), claims.getSubject());
        assertEquals(user.getId().toString(), claims.getId());
        assertThat(claims.getExpiration(), greaterThan(claims.getIssuedAt()));
        if (!userDetails.getAuthorities().isEmpty()) {
            assertTrue(claims.containsKey(SecurityConstants.JWT_ROLE_CLAIM));
            final String[] roles = claims.get(SecurityConstants.JWT_ROLE_CLAIM, String.class)
                                         .split(SecurityConstants.JWT_ROLE_DELIMITER);
            for (String role : roles) {
                assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(role)));
            }
        }
    }

    @Test
    void generateTokenCreatesJwtForUserWithAuthorities() {
        final Set<GrantedAuthority> authorities = ROLES.stream().map(SimpleGrantedAuthority::new)
                                                       .collect(Collectors.toSet());
        final DemoUserDetails userDetails = new DemoUserDetails(user, authorities);
        final String jwtToken = sut.generateToken(userDetails);
        verifyJWToken(jwtToken, userDetails);
    }

    @Test
    void extractUserInfoExtractsDataOfUserWithoutAuthoritiesFromJWT() {
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setId(user.getId().toString())
                                 .setIssuedAt(new Date())
                                 .setExpiration(
                                         new Date(System.currentTimeMillis() + SecurityConstants.SESSION_TIMEOUT))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();

        final DemoUserDetails result = sut.extractUserInfo(token);
        assertEquals(user, result.getUser());
        assertEquals(0, result.getAuthorities().size());
    }

    @Test
    void extractUserInfoExtractsDataOfUserWithAuthoritiesFromJWT() {
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setId(user.getId().toString())
                                 .setIssuedAt(new Date())
                                 .setExpiration(
                                         new Date(System.currentTimeMillis() + SecurityConstants.SESSION_TIMEOUT))
                                 .claim(SecurityConstants.JWT_ROLE_CLAIM,
                                         String.join(SecurityConstants.JWT_ROLE_DELIMITER, ROLES))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();

        final DemoUserDetails result = sut.extractUserInfo(token);
        ROLES.forEach(r -> assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority(r))));
    }

    @Test
    void extractUserInfoThrowsJwtExceptionWhenTokenCannotBeParsed() {
        final String token = "bblablalbla";
        final JwtException ex = assertThrows(JwtException.class, () -> sut.extractUserInfo(token));
        assertThat(ex.getMessage(), containsString("Unable to parse the specified JWT."));
    }

    @Test
    void extractUserInfoThrowsJwtExceptionWhenUserIdentifierIsNotInteger() {
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setId("_:123")
                                 .setIssuedAt(new Date())
                                 .setExpiration(
                                         new Date(System.currentTimeMillis() + SecurityConstants.SESSION_TIMEOUT))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        assertThrows(JwtException.class, () -> sut.extractUserInfo(token));
    }

    @Test
    void extractUserInfoThrowsIncompleteJwtExceptionWhenUsernameIsMissing() {
        final String token = Jwts.builder().setId(user.getId().toString())
                                 .setIssuedAt(new Date())
                                 .setExpiration(
                                         new Date(System.currentTimeMillis() + SecurityConstants.SESSION_TIMEOUT))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        final IncompleteJwtException ex = assertThrows(IncompleteJwtException.class, () -> sut.extractUserInfo(token));
        assertThat(ex.getMessage(), containsString("subject"));
    }

    @Test
    void extractUserInfoThrowsIncompleteJwtExceptionWhenIdentifierIsMissing() {
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setIssuedAt(new Date())
                                 .setExpiration(
                                         new Date(System.currentTimeMillis() + SecurityConstants.SESSION_TIMEOUT))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        final IncompleteJwtException ex = assertThrows(IncompleteJwtException.class, () -> sut.extractUserInfo(token));
        assertThat(ex.getMessage(), containsString("id"));
    }

    @Test
    void extractUserInfoThrowsTokenExpiredExceptionWhenExpirationIsInPast() {
        final String token = Jwts.builder().setId(user.getId().toString())
                                 .setSubject(user.getUsername())
                                 .setIssuedAt(new Date())
                                 .setExpiration(new Date(System.currentTimeMillis() - 1000))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        assertThrows(TokenExpiredException.class, () -> sut.extractUserInfo(token));
    }

    @Test
    void extractUserInfoThrowsTokenExpiredExceptionWhenExpirationIsMissing() {
        final String token = Jwts.builder().setId(user.getId().toString())
                                 .setSubject(user.getUsername())
                                 .setIssuedAt(new Date())
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
        assertThrows(TokenExpiredException.class, () -> sut.extractUserInfo(token));
    }

    @Test
    void refreshTokenUpdatesIssuedDate() {
        final Date oldIssueDate = new Date(System.currentTimeMillis() - 10000);
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setId(user.getId().toString())
                                 .setIssuedAt(oldIssueDate)
                                 .setExpiration(new Date(oldIssueDate.getTime() + SecurityConstants.SESSION_TIMEOUT))
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();

        final String result = sut.refreshToken(token);
        final Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(result)
                                  .getBody();
        assertTrue(claims.getIssuedAt().after(oldIssueDate));
    }

    @Test
    void refreshTokenUpdatesExpirationDate() {
        final Date oldIssueDate = new Date();
        final Date oldExpiration = new Date(oldIssueDate.getTime() + 10000);
        final String token = Jwts.builder().setSubject(user.getUsername())
                                 .setId(user.getId().toString())
                                 .setIssuedAt(oldIssueDate)
                                 .setExpiration(oldExpiration)
                                 .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();

        final String result = sut.refreshToken(token);
        final Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(result)
                                  .getBody();
        assertTrue(claims.getExpiration().after(oldExpiration));
    }
}