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

## Sample Data

On startup, the application generates two sample user accounts with the following credentials (stored encoded using _BCrypt_):

- `lasky@unsc.org/infinity`
- `palmer@unsc.org/spartans`

## Request/Response Examples

Login*:

```
POST /demo/j_spring_security_check HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username=lasky%40unsc.org&password=infinity
```

```
Access-Control-Expose-Headers: Authorization
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsYXNreUB1bnNjLm9yZyIsImp0aSI6IjEiLCJpYXQiOjE1MzY0OTkyMjIsImV4cCI6MTUzNjQ5OTgyMiwicm9sZSI6IlJPTEVfVVNFUiJ9.pTOMEi5QZRE7gxuKo0zxWueXli_XUsbo1p8Yzho7gGUAAUzWbnhIyO3_kJP64BhKctKQw9_Rn5MuqdCwgjy7mA
Content-Length: 60

{"loggedIn":true,"username":"lasky@unsc.org","success":true}
```

Get current user*:

```
GET /demo/rest/users/current HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsYXNreUB1bnNjLm9yZyIsImp0aSI6IjEiLCJpYXQiOjE1MzY0OTc4MTUsImV4cCI6MTUzNjQ5ODQxNSwicm9sZSI6IlJPTEVfVVNFUiJ9.8uYtembRUZBQxZI_YhxefpcsLmxPcXgF-nMY3S5ad8Rd4JtLd8GeVo5Oq0V9FK9gdSt-DnXI_uteBAm2YjRnpg
```

```
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Length: 200

{"id":1,"firstName":"Thomas","lastName":"Lasky","username":"lasky@unsc.org"}
```

_* Only relevant response headers are shown._

## Implementation Notes

- `JwtAuthorizationFilter` has to handle exceptions by itself because it is called before the request is passed to
the dispatcher servlet. Thus, exception handlers cannot be called. For this reason, `JwtAuthorizationFilter` writes error
info directly into response body and stops the filter chain processing.

- The demo currently uses PostgreSQL as its main database, but switching to another database is a matter of editing `pom.xml`
and `application.properties`.


