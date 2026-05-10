package com.hyperativa.card.controller;

import com.hyperativa.card.contract.ApiApi;
import com.hyperativa.card.dto.CardRequest;
import com.hyperativa.card.dto.CardResponse;
import com.hyperativa.card.service.CardService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class CardController implements ApiApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardResponse> getCardsByClient(String clientName) {
        log.info("getCardsByClient...");
        CardResponse response = new CardResponse();
        response.setClients(cardService.getCardsByClient(clientName));
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<CardResponse> registerCardForClient(String clientName, String cardNumber) {

        cardService.save(clientName, cardNumber);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CardResponse> registerCard(@NonNull CardRequest request) {
        cardService.save(request.getClients());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> importCards() {
        cardService.importCardsFromFile();
        return ResponseEntity.accepted().build();
    }
}
