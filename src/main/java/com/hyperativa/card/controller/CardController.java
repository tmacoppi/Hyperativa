package com.hyperativa.card.controller;

import com.hyperativa.card.contract.ApiApi;
import com.hyperativa.card.dto.CardRequest;
import com.hyperativa.card.dto.CardResponse;
import com.hyperativa.card.service.CardService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class CardController implements ApiApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<List<CardResponse>> getCardsByClient(String clientName) {
        return ApiApi.super.getCardsByClient(clientName);
    }

    @Override
    public ResponseEntity<CardResponse> registerCardForClient(String clientName, String cardNumber) {

        cardService.save(clientName, cardNumber);
        return ResponseEntity.ok().build();
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
