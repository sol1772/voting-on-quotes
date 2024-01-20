package com.kameleoon.voting_on_quotes.controller;

import com.kameleoon.voting_on_quotes.model.Quote;
import com.kameleoon.voting_on_quotes.model.dto.PageRequestDto;
import com.kameleoon.voting_on_quotes.service.QuoteService;
import com.kameleoon.voting_on_quotes.util.AppErrorResponse;
import com.kameleoon.voting_on_quotes.util.AppRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.kameleoon.voting_on_quotes.util.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("api/quotes")
public class QuoteController {
    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);
    private final static Random RANDOMIZER = new Random();
    private final QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    public ResponseEntity<List<Quote>> getAllQuotes() {
        List<Quote> quotes = quoteService.getAllQuotes();
        return ResponseEntity.ok().body(quotes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getOne(@PathVariable("id") Long id) {
        Optional<Quote> quote = quoteService.getQuoteById(id);
        return ResponseEntity.of(quote);
    }

    @GetMapping("/random")
    public ResponseEntity<Quote> getRandomOne() {
        return getOne(nextLong(1, quoteService.getQuoteRepository().count() + 1));
    }

    @PostMapping("/add")
    public ResponseEntity<Quote> addQuote(@RequestBody Quote quote, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }
        return getNewQuote(quote);
    }

    @PostMapping("/add-all")
    public ResponseEntity<List<Quote>> addQuotes(@RequestBody List<Quote> quotes, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }
        List<Quote> created = quoteService.createQuotes(quotes);
        return ResponseEntity.ok().body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quote> editQuote(@PathVariable Long id, @RequestBody Quote updatedQuote) {
        Optional<Quote> updated;
        try {
            updated = quoteService.updateQuote(id, updatedQuote);
        } catch (RuntimeException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when updating quote with id {}: {}", id, e.getMessage());
            }
            throw new AppRuntimeException(e.getMessage());
        }
        return updated.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> getNewQuote(updatedQuote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Quote> deleteQuote(@PathVariable("id") Long id) {
        quoteService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<Quote> voteOnQuote(@PathVariable Long id, @RequestParam(name = "voter") Long voterId,
                                             @RequestParam(name = "upvote") boolean upvote) {
        Optional<Quote> quote = quoteService.voteOnQuote(id, voterId, upvote);
        return ResponseEntity.of(quote);
    }

    @GetMapping("/top")
    public ResponseEntity<List<Quote>> findTop(@RequestParam(name = "user", required = false) Long userId) {
        List<Quote> quotes;
        PageRequestDto dto = new PageRequestDto();
        dto.setSort(Sort.Direction.DESC);
        dto.setSortByColumn("score");
        if (Objects.isNull(userId)) {
            quotes = quoteService.getPageAllQuotes(dto).getContent();
        } else {
            quotes = quoteService.getPageQuotesByUser(dto, userId).getContent();
        }
        return ResponseEntity.ok().body(quotes);
    }

    @GetMapping("/flop")
    public ResponseEntity<List<Quote>> findFlop(@RequestParam(name = "user", required = false) Long userId) {
        List<Quote> quotes;
        PageRequestDto dto = new PageRequestDto();
        dto.setSort(Sort.Direction.ASC);
        dto.setSortByColumn("score");
        if (Objects.isNull(userId)) {
            quotes = quoteService.getPageAllQuotes(dto).getContent();
        } else {
            quotes = quoteService.getPageQuotesByUser(dto, userId).getContent();
        }
        return ResponseEntity.ok().body(quotes);
    }

    private long nextLong(long lowerRange, long upperRange) {
        return (long) (RANDOMIZER.nextDouble() * (upperRange - lowerRange)) + lowerRange;
    }

    public ResponseEntity<Quote> getNewQuote(@RequestBody Quote quote) {
        Quote created = quoteService.createQuote(quote);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @ExceptionHandler
    private ResponseEntity<AppErrorResponse> handleException(AppRuntimeException e) {
        AppErrorResponse response = new AppErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
