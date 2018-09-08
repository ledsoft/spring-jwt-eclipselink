package com.github.ledsoft.demo.security;

public class SecurityConstants {

    public static final String SECURITY_CHECK_URI = "/j_spring_security_check";

    public static final int SESSION_TIMEOUT = 30 * 60 * 1000;

    /**
     * Header specifying which security-related headers should be accessible to the client application in browser.
     * <p>
     * This header's value must include the authentication header ({@link #AUTHENTICATION_HEADER}), otherwise,
     * application is not able to extract the authentication token from response.
     */
    public static final String EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";

    /**
     * Request/response header used to store authentication info.
     */
    public static final String AUTHENTICATION_HEADER = "Authentication";

    /**
     * String prefix added to JWT tokens in the {@link #AUTHENTICATION_HEADER}.
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
