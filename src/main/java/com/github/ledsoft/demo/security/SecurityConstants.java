package com.github.ledsoft.demo.security;

public class SecurityConstants {

    public static final String SECURITY_CHECK_URI = "/j_spring_security_check";

    /**
     * Set a short (more secure) token expiration timeout.
     */
    public static final int TOKEN_EXPIRATION_TIMEOUT = 10 * 60 * 1000;

    /**
     * Header specifying which security-related headers should be accessible to the client application in browser.
     * <p>
     * This header's value must include the Authorization header, otherwise,
     * client JS application may not able to extract the authentication token from response.
     */
    public static final String EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";

    /**
     * String prefix added to JWT tokens in the {@link org.springframework.http.HttpHeaders#AUTHORIZATION} header.
     */
    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    public static final String JWT_ROLE_CLAIM = "role";

    public static final String JWT_SECRET = "t1m3sh33t3r";

    public static final String JWT_ROLE_DELIMITER = "-";

    public static final String USERNAME_PARAM = "username";

    private SecurityConstants() {
        throw new AssertionError();
    }
}
