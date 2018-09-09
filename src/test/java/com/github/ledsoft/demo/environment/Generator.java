package com.github.ledsoft.demo.environment;

import com.github.ledsoft.demo.model.User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Random;

public class Generator {

    private static final Random RANDOM = new Random();

    private Generator() {
        throw new AssertionError();
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setUsername(user.getFirstName() + "." + user.getLastName() + "@timesheeter.org");
        user.setPassword("password" + randomInt());
        return user;
    }

    public static MockHttpServletRequest mockRequest() {
        return new MockHttpServletRequest();
    }

    public static MockHttpServletResponse mockResponse() {
        return new MockHttpServletResponse();
    }
}
