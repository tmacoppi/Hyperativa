package com.hyperativa.card.controller;

import com.hyperativa.card.contract.ApiApi;
import com.hyperativa.card.dto.CardRequest;
import com.hyperativa.card.dto.CardResponse;
import com.hyperativa.card.service.CardService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CardController implements ApiApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardResponse> getCardsByClient(String clientName) {
        log.info("getCardsByClient - clientName: {}", clientName);
        CardResponse response = new CardResponse();
        response.setClients(cardService.getCardsByClient(clientName));
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<CardResponse> registerCardForClient(String clientName, String cardNumber) {
        log.info("registerCardForClient - clientName: {}, cardNumber: {}", clientName, cardNumber);
        CardResponse response = new CardResponse();
        response.setClients(cardService.save(clientName, cardNumber));
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<CardResponse> registerCard(@NonNull CardRequest request) {
        log.info("registerCard - request: {}", request);
        CardResponse response = new CardResponse();
        response.setClients(cardService.save(request.getClients()));
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<Void> importCards() {
        log.info("importCards...");
        cardService.importCardsFromFile();
        return ResponseEntity.accepted().build();
    }
}
