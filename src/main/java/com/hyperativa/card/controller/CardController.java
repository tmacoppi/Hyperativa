package com.hyperativa.card.controller;

import com.hyperativa.card.contract.ApiApi;
import com.hyperativa.card.dto.CardRequest;
import com.hyperativa.card.dto.CardResponse;
import com.hyperativa.card.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController implements ApiApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardResponse> registerCard(CardRequest request) {
        // Os métodos getClientName() e getCardNumber() foram gerados no DTO
        cardService.save(request.getClients());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> importCards() {
        cardService.importCardsFromFile();
        return ResponseEntity.accepted().body("Processamento de importação iniciado em segundo plano.");
    }
}
