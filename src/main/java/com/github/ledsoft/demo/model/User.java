package com.github.ledsoft.demo.model;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "APP_USER")
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    @Basic(optional = false)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Basic(optional = false)
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Basic(optional = false)
    @Column(nullable = false)
    private String password;

    public User() {
    }

    public User(@NotBlank String firstName, @NotBlank String lastName,
                @NotBlank String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + "<" + username + ">";
    }
}
