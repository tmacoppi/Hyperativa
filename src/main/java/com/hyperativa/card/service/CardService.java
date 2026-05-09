package com.hyperativa.card.service;

import com.hyperativa.card.dto.Card;
import com.hyperativa.card.dto.Client;
import com.hyperativa.card.mapper.CardMapper;
import com.hyperativa.card.mapper.ClientMapper;
import com.hyperativa.card.model.CardEntity;
import com.hyperativa.card.model.ClientEntity;
import com.hyperativa.card.repository.CardRepository;
import com.hyperativa.card.repository.ClientRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardService {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;
    private final ClientMapper clientMapper;
    private final CardMapper cardMapper;

    public CardService(ClientRepository clientRepository,
                       CardRepository cardRepository,
                       ClientMapper clientMapper,
                       CardMapper cardMapper){
        this.clientRepository = clientRepository;
        this.cardRepository = cardRepository;
        this.clientMapper = clientMapper;
        this.cardMapper = cardMapper;
    }

    public void save(@NonNull List<Client> clients) {
        ClientEntity clientEntity = null;
        List<CardEntity> cardList = new ArrayList<>();
        CardEntity cardEntity = null;

        for (Client client : clients){
            clientEntity = clientRepository.findByName( client.getName());
            if (clientEntity == null) {
                clientEntity = clientMapper.toEntity(client);
                clientRepository.save(clientEntity);
            }

            for (Card card : client.getCards()){
                cardEntity = cardMapper.toEntity(card);
                cardEntity.setIdClient(clientEntity.getId());
                if (!cardRepository.existsByCardNumber( cardEntity.getCardNumber() )){
                    cardList.add(cardEntity);
                }
            }

            cardRepository.saveAll(cardList);
        }

    }

    public void importCardsFromFile() {
        // Lógica para iniciar o processamento assíncrono do arquivo TXT
        System.out.println("Iniciando importação de cartões via arquivo...");
    }

}
