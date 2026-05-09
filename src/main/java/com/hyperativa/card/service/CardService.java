package com.hyperativa.card.service;

import com.hyperativa.card.dto.Card;
import com.hyperativa.card.dto.Client;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    public CardService(){}

    public void save(List<Client> clients) {
        // Lógica para salvar o cartão no banco de dados
        clients.forEach(client -> {
            System.out.println("Salvando cartoes: " + client.getCards().stream(). map(Card::getNumber).toList() + " para o cliente: " + client.getName());

        });
    }

    public void importCardsFromFile() {
        // Lógica para iniciar o processamento assíncrono do arquivo TXT
        System.out.println("Iniciando importação de cartões via arquivo...");
    }

}
