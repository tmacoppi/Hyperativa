package com.hyperativa.card.controller;

import com.hyperativa.card.contract.ApiApi;
import com.hyperativa.card.dto.CardRequest;
import com.hyperativa.card.dto.CardResponse;
import com.hyperativa.card.service.CardService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController implements ApiApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardResponse> registerCard(@NonNull CardRequest request) {
        // Os métodos getClientName() e getCardNumber() foram gerados no DTO
        cardService.save(request.getClients());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> importCards() {
        cardService.importCardsFromFile();
        return ResponseEntity.accepted().build();
    }
}
