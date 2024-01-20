package com.kameleoon.voting_on_quotes.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kameleoon.voting_on_quotes.util.DateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quote implements Serializable {
    @Serial
    private static final long serialVersionUID = 2105122041958251257L;

    @JsonManagedReference(value = "quote-vote")
    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private final List<Vote> votes = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference(value = "user-quote")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private String content;

    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    private long upVotes;
    private long downVotes;
    private long score;

    public Long getUpVotes() {
        return votes.stream().filter(Vote::isUpvote).count();
    }

    public void setUpVotes() {
        this.upVotes = votes.stream().filter(Vote::isUpvote).count();
    }

    public Long getDownVotes() {
        return votes.stream().filter(t -> !t.isUpvote()).count();
    }

    public void setDownVotes() {
        this.downVotes = votes.stream().filter(t -> !t.isUpvote()).count();
    }

    public Long getScore() {
        return votes.stream().filter(Vote::isUpvote).count() - votes.stream().filter(t -> !t.isUpvote()).count();
    }

    public void setScore() {
        this.score = votes.stream().filter(Vote::isUpvote).count() - votes.stream().filter(t -> !t.isUpvote()).count();
    }

    public void increaseUpVotes() {
        this.upVotes++;
        updateScore();
    }

    public void increaseDownVotes() {
        this.downVotes++;
        updateScore();
    }

    private void updateScore() {
        this.score = this.upVotes - this.downVotes;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Objects.equals(id, quote.id) && Objects.equals(user, quote.user)
                && Objects.equals(content, quote.content) && Objects.equals(createdAt, quote.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, content, createdAt);
    }
}
