package com.hyperativa.card.service;

import org.springframework.stereotype.Service;

@Service
public class CardService {

    public CardService(){}

    public void save(String clientName, String cardNumber) {
        // Lógica para salvar o cartão no banco de dados
        System.out.println("Salvando cartão: " + cardNumber + " para o cliente: " + clientName);
    }

    public void importCardsFromFile() {
        // Lógica para iniciar o processamento assíncrono do arquivo TXT
        System.out.println("Iniciando importação de cartões via arquivo...");
    }

}
