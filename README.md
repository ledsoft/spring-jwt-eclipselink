# Spring JWT Eclipselink Demo

This demo/project template showcases usage of Spring security with JSON Web Tokens (JWT) backed
by Spring JPA repositories using Eclipselink.

The security solution is tested using JUnit 5 and Spring Boot test utilities.

The project was inspired by a by blog post by [Auth0](https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/)
and a Github repository [jwt-spring-security-demo](https://github.com/szerhusenBC/jwt-spring-security-demo).

## Technology Overview

- Spring Boot 2.x
- Spring JPA Repositories
- JSON Web Tokens
- Eclipselink
- JUnit Jupiter 5
- JSR 380 Validation API

The project uses Maven 3.x and is built using Java 10 (but no Java 10-specific features are used,
so it should be fairly easy to use it with Java 8 as well).

## Implementation Notes

- `JwtAuthorizationFilter` has to handle exceptions by itself because it is called before the request is passed to
the dispatcher servlet. Thus, exception handlers cannot be called. For this reason, `JwtAuthorizationFilter` writes error
info directly into response body and stops the filter chain processing.

- The demo currently uses PostgreSQL as its main database, but switching to another database is a matter of editing `pom.xml`
and `application.properties`.


