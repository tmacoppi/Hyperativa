package com.hyperativa.card.service;

import com.hyperativa.card.dto.Card;
import com.hyperativa.card.dto.Client;
import com.hyperativa.card.mapper.CardMapper;
import com.hyperativa.card.mapper.ClientMapper;
import com.hyperativa.card.model.CardEntity;
import com.hyperativa.card.model.ClientEntity;
import com.hyperativa.card.model.ImportEntity;
import com.hyperativa.card.repository.CardRepository;
import com.hyperativa.card.repository.ClientRepository;
import com.hyperativa.card.repository.ImportRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CardService {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;
    private final ImportRepository importRepository;
    private final ClientMapper clientMapper;
    private final CardMapper cardMapper;

    private static final int BATCH_SIZE = 1000;

    public CardService(ClientRepository clientRepository,
                       CardRepository cardRepository,
                       ImportRepository importRepository,
                       ClientMapper clientMapper,
                       CardMapper cardMapper){
        this.clientRepository = clientRepository;
        this.cardRepository = cardRepository;
        this.importRepository = importRepository;
        this.clientMapper = clientMapper;
        this.cardMapper = cardMapper;
    }

    public List<Client> getCardsByClient(String clientName){
        List<Client> clients = new ArrayList<>();

        ClientEntity clientEntity = clientRepository.findByName(clientName);
        if (clientEntity == null) {
            return null;
        }

        Client client = clientMapper.toDto(clientEntity);
        List<CardEntity> cardEntities = cardRepository.findAllByIdClient(clientEntity.getId());
        List<Card> cards = new ArrayList<>();

        for (CardEntity cardEntity : cardEntities) {
            Card card = cardMapper.toDto(cardEntity);
            cards.add(card);
        }

        client.setCards(cards);
        clients.add(client);

        return clients;
    }

    public List<Client> save(String clientName, String cardNumber){
        List<Client> clients = new ArrayList<>();

        ClientEntity clientEntity = clientRepository.findByName(clientName);
        if (clientEntity == null) {
            clientEntity = new ClientEntity();
            clientEntity.setName(clientName);
            clientEntity.setDate(LocalDateTime.now());
            clientRepository.save(clientEntity);
        }

        Client client = clientMapper.toDto(clientEntity);

        CardEntity cardEntity = new CardEntity();
        cardEntity.setIdClient(clientEntity.getId());
        cardEntity.setCardNumber(cardNumber);
        cardEntity.setDate(LocalDateTime.now());
        cardRepository.save(cardEntity);

        List<Card> cards = new ArrayList<>();
        Card card = cardMapper.toDto(cardEntity);

        cards.add(card);
        client.setCards(cards);
        clients.add(client);

        return clients;
    }

    public List<Client> save(@NonNull List<Client> clients) {
        List<Client> clients1 = new ArrayList<>();
        Client client1;
        ClientEntity clientEntity = null;
        CardEntity cardEntity = null;
        List<CardEntity> cardList;

        for (Client client : clients){
            cardList = new ArrayList<>();
            clientEntity = clientRepository.findByName( client.getName());
            if (clientEntity == null) {
                clientEntity = clientMapper.toEntity(client);
                clientEntity.setDate(LocalDateTime.now());
                clientRepository.save(clientEntity);
            }

            client1 = clientMapper.toDto(clientEntity);

            for (Card card : client.getCards()){
                cardEntity = cardMapper.toEntity(card);
                cardEntity.setIdClient(clientEntity.getId());
                cardEntity.setDate(LocalDateTime.now());

                if (!cardRepository.existsByCardNumber( cardEntity.getCardNumber() )){
                    cardList.add(cardEntity);
                }
            }

            cardRepository.saveAll(cardList);

            cardList = cardRepository.findAllByIdClient(clientEntity.getId());

            client1.setCards(cardMapper.toDto(cardList));
            clients1.add(client1);
        }

        return clients1;
    }

    @Async
    public void importCardsFromFile() {
        log.info("Iniciando importação na Virtual Thread: {}", Thread.currentThread().getName());

        Path filePath = Paths.get("C:\\Dev\\import\\hyperativa\\DESAFIO-HYPERATIVA.txt");

        ClientEntity clientEntity = null;
        CardEntity cardEntity = null;
        List<CardEntity> cards;

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {

            String linhaAtual;

            while ((linhaAtual = reader.readLine()) != null) {

                if (linhaAtual.trim().isEmpty()) {
                    continue;
                }

                // [01-29]NOME   [30-37]DATA   [38-45]LOTE   [46-51]QTD DE REGISTROS
                String clientName = linhaAtual.substring(0, 29).trim();
                String clientDate = linhaAtual.substring(29, 37).trim();
                String lote = linhaAtual.substring(37, 45).trim();
                String qtdString = linhaAtual.substring(45, 51).trim();

                clientEntity = clientRepository.findByName( clientName);
                if (clientEntity == null) {
                    clientEntity = new ClientEntity();
                    clientEntity.setName(clientName);
                    clientEntity.setDate(LocalDateTime.now());
                    clientRepository.save(clientEntity);
                }

                ImportEntity importEntity = new ImportEntity();
                importEntity.setIdClient(clientEntity.getId());
                importEntity.setFileName(filePath.getFileName().toString());
                importEntity.setFileDate(LocalDate.of(
                        Integer.parseInt(clientDate.substring(0,4)),
                        Integer.parseInt(clientDate.substring(4,6)),
                        Integer.parseInt(clientDate.substring(6,8))));
                importEntity.setDate(LocalDateTime.now());
                importEntity.setChunk(lote);
                importRepository.save(importEntity);

                cards = new ArrayList<>();

                int qtdCartoes = Integer.parseInt(qtdString);

                log.info("Processando cliente: {} com {} cartões.", clientName, qtdCartoes);

                for (int i = 0; i < qtdCartoes; i++) {
                    cardEntity = new CardEntity();

                    String linhaDetalhe = reader.readLine();

                    // Proteção caso o arquivo acabe no meio do bloco de forma inesperada
                    if (linhaDetalhe == null) {
                        log.error("Arquivo terminou prematuramente antes de ler todos os cartões do cliente {}", clientName);
                        break;
                    }

                    // [01-01]IDENTIFICADOR DA LINHA   [02-07]NUMERAÇÃO NO LOTE   [08-26]NÚMERO DE CARTAO COMPLETO
                    String lineId = linhaDetalhe.substring(0, 1).trim();
                    String numeroLote = linhaDetalhe.substring(1, 7).trim();
                    String cartaoNumero = "";
                    if (linhaDetalhe.length() > 50)
                        cartaoNumero = linhaDetalhe.substring(7, 51).trim();
                    else
                        cartaoNumero = linhaDetalhe.substring(7).trim();

                    cardEntity.setCardNumber(cartaoNumero);
                    cardEntity.setIdImport(importEntity.getId());
                    cardEntity.setDate(LocalDateTime.now());
                    cardEntity.setIdClient(clientEntity.getId());
                    cards.add(cardEntity);
                }

                cardRepository.saveAll(cards);

                // 3. LÊ O RESUMO (Trailer)
                String linhaResumo = reader.readLine();

                if (linhaResumo != null) {
                    // Extrai os dados do resumo (Exemplo: posições 0-10 Totalizadores)
                    String infoResumo = linhaResumo.substring(0, 10).trim();
                }
            }

            log.info("Processamento assíncrono finalizado com sucesso.");

        } catch (IOException e) {
            log.error("Erro crítico ao ler o arquivo TXT: {}", e.getMessage());
        } catch (NumberFormatException e) {
            log.error("Erro de formatação posicional (não foi possível converter para número): {}", e.getMessage());
        }
    }

}
