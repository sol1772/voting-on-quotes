package com.kameleoon.voting_on_quotes.repository;

import com.kameleoon.voting_on_quotes.model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Nonnull
    @EntityGraph(attributePaths = {"quotes"})
    Page<User> findAll(@Nonnull Pageable pageable);

    @Nonnull
    @EntityGraph(attributePaths = {"quotes"})
    Optional<User> findById(@Nonnull Long id);

    @EntityGraph(attributePaths = {"quotes"})
    User findUserById(@Param("id") Long id);

    User findUserByEmail(String email);
}
