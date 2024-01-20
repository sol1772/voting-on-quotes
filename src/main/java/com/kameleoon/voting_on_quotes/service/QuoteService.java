package com.kameleoon.voting_on_quotes.service;

import com.kameleoon.voting_on_quotes.model.Quote;
import com.kameleoon.voting_on_quotes.model.User;
import com.kameleoon.voting_on_quotes.model.Vote;
import com.kameleoon.voting_on_quotes.model.dto.PageRequestDto;
import com.kameleoon.voting_on_quotes.repository.QuoteRepository;
import com.kameleoon.voting_on_quotes.repository.UserRepository;
import com.kameleoon.voting_on_quotes.util.AppRuntimeException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Getter
@Transactional(readOnly = true)
public class QuoteService {
    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;

    public QuoteService(QuoteRepository quoteRepository, UserRepository userRepository) {
        this.quoteRepository = quoteRepository;
        this.userRepository = userRepository;
    }

    public Optional<Quote> getQuoteById(Long id) {
        return quoteRepository.findById(id);
    }

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @Transactional
    public Quote createQuote(Quote quote) {
        Quote dbQuote = quoteRepository.save(quote);
        if (logger.isInfoEnabled()) {
            logger.info("Created quote {}", dbQuote);
        }
        return dbQuote;
    }

    @Transactional
    public List<Quote> createQuotes(List<Quote> quotes) {
        List<Quote> dbQuotes = quoteRepository.saveAll(quotes);
        if (logger.isInfoEnabled()) {
            logger.info("Created quotes {}", dbQuotes);
        }
        return dbQuotes;
    }

    @Transactional
    public Optional<Quote> updateQuote(Long id, Quote updatedQuote) {
        Optional<Quote> updated = quoteRepository.findById(id).map(oldQuote -> quoteRepository.save(updatedQuote));
        if (logger.isInfoEnabled()) {
            updated.ifPresent(n -> logger.info("Updated quote {}", updated.get()));
        }
        return updated;
    }

    @Transactional
    public void deleteQuote(Long id) {
        try {
            quoteRepository.deleteById(id);
            if (logger.isInfoEnabled()) {
                logger.info("Deleted quote with id {}", id);
            }
        } catch (EmptyResultDataAccessException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when deleting the quote with id {}", id);
            }
        }
    }

    @Transactional
    public Optional<Quote> voteOnQuote(Long id, Long voterId, boolean upvote) {
        Optional<Quote> quote = quoteRepository.findById(id);
        Optional<User> voter = userRepository.findById(voterId);
        if (quote.isPresent() && voter.isPresent()) {
            try {
                Vote vote = new Vote(null, quote.get(), voter.get(), upvote);
                quote.get().getVotes().add(vote);
                if (upvote) {
                    quote.get().increaseUpVotes();
                } else {
                    quote.get().increaseDownVotes();
                }
                return updateQuote(id, quote.get());
            } catch (RuntimeException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Error when voting on quote with id {}: {}", id, e.getMessage());
                }
                throw new AppRuntimeException(e.getMessage());
            }
        }
        return quote;
    }

    public Page<Quote> getPageAllQuotes(PageRequestDto dto) {
        Pageable pageable = new PageRequestDto().getPageable(dto);
        return quoteRepository.findAll(pageable);
    }

    public Page<Quote> getPageQuotesByUser(PageRequestDto dto, Long id) {
        Pageable pageable = new PageRequestDto().getPageable(dto);
        return quoteRepository.findQuotesByUserId(id, pageable);
    }
}
