package com.kameleoon.voting_on_quotes.repository;

import com.kameleoon.voting_on_quotes.model.Quote;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    @Nonnull
    @EntityGraph(attributePaths = {"votes"})
    Page<Quote> findAll(@Nonnull Pageable pageable);

    @Nonnull
    @EntityGraph(attributePaths = {"votes"})
    Optional<Quote> findById(@Nonnull Long id);

    @EntityGraph(attributePaths = {"votes"})
    Quote findQuoteById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"votes"})
    Page<Quote> findQuotesByUserId(@Param("id") Long id, Pageable pageable);
}
