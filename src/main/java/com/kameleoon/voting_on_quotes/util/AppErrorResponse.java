package com.kameleoon.voting_on_quotes.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppErrorResponse {
    private String message;
    private long timestamp;
}
