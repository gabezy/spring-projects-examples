package br.com.gabezy.cashcard.domain;

import org.springframework.data.annotation.Id;

public record CashCard(
        @Id
        Long id,
        Double amount
) {
}
