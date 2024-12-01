package br.com.gabezy.cashcard.controller;

import br.com.gabezy.cashcard.domain.CashCard;
import br.com.gabezy.cashcard.repository.CashCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        var cashCardOptinal = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        return cashCardOptinal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        PageRequest pageRequest = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount")));

        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(), pageRequest);

        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, cashCardRequest.amount(), principal.getName());
        var savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationURI = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationURI).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCard, Principal principal) {
        var cashCardToUpdate = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        if (cashCardToUpdate.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var cashCardUpdated = new CashCard(cashCardToUpdate.get().id(), cashCard.amount(), principal.getName());
        cashCardRepository.save(cashCardUpdated);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long requestedId, Principal principal) {
        boolean exists = cashCardRepository.existsByIdAndOwner(requestedId, principal.getName());

        if (exists) {
            cashCardRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
