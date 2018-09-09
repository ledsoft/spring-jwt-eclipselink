package com.github.ledsoft.demo.security;

import com.github.ledsoft.demo.exception.IncompleteJwtException;
import com.github.ledsoft.demo.exception.JwtException;
import com.github.ledsoft.demo.exception.TokenExpiredException;
import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    /**
     * Generates a JSON Web Token for the specified authenticated user.
     *
     * @param userDetails User info
     * @return Generated JWT has
     */
    public String generateToken(DemoUserDetails userDetails) {
        final Date issued = new Date();
        return Jwts.builder().setSubject(userDetails.getUsername())
                   .setId(userDetails.getUser().getId().toString())
                   .setIssuedAt(issued)
                   .setExpiration(new Date(issued.getTime() + SecurityConstants.SESSION_TIMEOUT))
                   .claim(SecurityConstants.JWT_ROLE_CLAIM, mapAuthoritiesToClaim(userDetails.getAuthorities()))
                   .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                   .compact();
    }

    private static String mapAuthoritiesToClaim(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority)
                          .collect(Collectors.joining(SecurityConstants.JWT_ROLE_DELIMITER));
    }

    /**
     * Retrieves user info from the specified JWT.
     * <p>
     * The token is first validated for correct format and expiration date.
     *
     * @param token JWT to read
     * @return User info retrieved from the specified token
     */
    public DemoUserDetails extractUserInfo(String token) {
        Objects.requireNonNull(token);
        try {
            final Claims claims = getClaimsFromToken(token);
            verifyAttributePresence(claims);
            final User user = new User();
            user.setId(Integer.parseInt(claims.getId()));
            user.setUsername(claims.getSubject());
            final String roles = claims.get(SecurityConstants.JWT_ROLE_CLAIM, String.class);
            return new DemoUserDetails(user, mapClaimToAuthorities(roles));
        } catch (NumberFormatException e) {
            throw new JwtException("Unable to parse user identifier from the specified JWT.", e);
        }
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET)
                       .parseClaimsJws(token).getBody();
        } catch (MalformedJwtException e) {
            throw new JwtException("Unable to parse the specified JWT.", e);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException(e.getMessage());
        }
    }

    private static void verifyAttributePresence(Claims claims) {
        if (claims.getSubject() == null) {
            throw new IncompleteJwtException("JWT is missing subject.");
        }
        if (claims.getId() == null) {
            throw new IncompleteJwtException("JWT is missing id.");
        }
        if (claims.getExpiration() == null) {
            throw new TokenExpiredException("Missing token expiration info. Assuming expired.");
        }
    }

    private static List<GrantedAuthority> mapClaimToAuthorities(String claim) {
        if (claim == null) {
            return Collections.emptyList();
        }
        final String[] roles = claim.split(SecurityConstants.JWT_ROLE_DELIMITER);
        final List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    /**
     * Updates issuing and expiration date of the specified token, generating a new one.
     *
     * @param token The token to refresh
     * @return Newly generated token with updated expiration date
     */
    public String refreshToken(String token) {
        Objects.requireNonNull(token);
        final Claims claims = getClaimsFromToken(token);
        final Date issuedAt = new Date();
        claims.setIssuedAt(issuedAt);
        claims.setExpiration(new Date(issuedAt.getTime() + SecurityConstants.SESSION_TIMEOUT));
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                   .compact();
    }
}
