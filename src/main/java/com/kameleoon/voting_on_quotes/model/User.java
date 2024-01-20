package com.kameleoon.voting_on_quotes.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kameleoon.voting_on_quotes.util.DateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1105122041950251207L;

    @JsonManagedReference(value = "user-quote")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Quote> quotes = new ArrayList<>();

    @JsonManagedReference(value = "user-vote")
    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL)
    private final List<Vote> votes = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String email;

    private String passwordHash;

    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_TIME_PATTERN)
    private LocalDateTime registeredAt;

    public String hashPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && username.equals(user.username) && email.equals(user.email)
                && Objects.equals(registeredAt, user.registeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, registeredAt);
    }
}
