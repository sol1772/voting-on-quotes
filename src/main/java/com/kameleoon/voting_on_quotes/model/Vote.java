package com.kameleoon.voting_on_quotes.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"quoteId", "voterId"})})
public class Vote implements Serializable {
    @Serial
    private static final long serialVersionUID = 3105122041958251257L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference(value = "quote-vote")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quoteId")
    private Quote quote;

    @JsonBackReference(value = "user-vote")
    @ManyToOne
    @JoinColumn(name = "voterId")
    private User voter;

    private boolean upvote;

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", quote=" + quote +
                ", voter=" + voter +
                ", upvote=" + upvote +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return upvote == vote.upvote && Objects.equals(id, vote.id) && Objects.equals(quote, vote.quote)
                && Objects.equals(voter, vote.voter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quote, voter, upvote);
    }
}
