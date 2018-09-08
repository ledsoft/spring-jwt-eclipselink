package com.github.ledsoft.demo.security;

import com.github.ledsoft.demo.model.User;
import com.github.ledsoft.demo.security.model.AuthenticationToken;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class SecurityUtils {

    /**
     * Allows to access the currently logged in user.
     *
     * @return Currently logged in user
     */
    public static User getCurrentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        assert context != null;
        final DemoUserDetails userDetails = (DemoUserDetails) context.getAuthentication().getDetails();
        return userDetails.getUser();
    }

    /**
     * Creates an authentication token based on the specified user details and sets it to the current thread's security
     * context.
     *
     * @param userDetails Details of the user to set as current
     * @return The generated authentication token
     */
    public static AuthenticationToken setCurrentUser(DemoUserDetails userDetails) {
        final AuthenticationToken token = new AuthenticationToken(userDetails);
        token.setAuthenticated(true);

        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        return token;
    }
}
