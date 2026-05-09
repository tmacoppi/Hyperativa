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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CardService {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;
    private final ClientMapper clientMapper;
    private final CardMapper cardMapper;

    private static final int BATCH_SIZE = 1000;

    public CardService(ClientRepository clientRepository,
                       CardRepository cardRepository,
                       ClientMapper clientMapper,
                       CardMapper cardMapper){
        this.clientRepository = clientRepository;
        this.cardRepository = cardRepository;
        this.clientMapper = clientMapper;
        this.cardMapper = cardMapper;
    }

    public void save(String clientName, String cardNumber){
        ClientEntity clientEntity = clientRepository.findByName(clientName);
        if (clientEntity == null) {
            clientEntity = new ClientEntity();
            clientEntity.setName(clientName);
            clientEntity.setDate(LocalDateTime.now());
            clientRepository.save(clientEntity);
        }

        CardEntity cardEntity = new CardEntity();
        cardEntity.setIdClient(clientEntity.getId());
        cardEntity.setCardNumber(cardNumber);
        cardEntity.setDate(LocalDateTime.now());
        cardRepository.save(cardEntity);
    }

    public void save(@NonNull List<Client> clients) {
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

            for (Card card : client.getCards()){
                cardEntity = cardMapper.toEntity(card);
                cardEntity.setIdClient(clientEntity.getId());
                cardEntity.setDate(LocalDateTime.now());

                if (!cardRepository.existsByCardNumber( cardEntity.getCardNumber() )){
                    cardList.add(cardEntity);
                }
            }

            cardRepository.saveAll(cardList);
        }

    }

    @Async
    public void importCardsFromFile() {
        System.out.println("Iniciando importação na Virtual Thread: " + Thread.currentThread().getName());

        Path filePath = Paths.get("C:\\Dev\\import\\hyperativa\\DESAFIO-HYPERATIVA.txt");

        List<Client> clients = new ArrayList<>();
        Client client = null;
        List<Card> cards = null;
        Card card = null;

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

                client = new Client();
                client.setName(clientName);

                cards = new ArrayList<>();

                int qtdCartoes = Integer.parseInt(qtdString);

                System.out.println("Processando cliente: " + clientName + " com " + qtdCartoes + " cartões.");

                for (int i = 0; i < qtdCartoes; i++) {
                    System.out.println("i: " + i);

                    card = new Card();

                    String linhaDetalhe = reader.readLine();

                    // Proteção caso o arquivo acabe no meio do bloco de forma inesperada
                    if (linhaDetalhe == null) {
                        System.err.println("Arquivo terminou prematuramente antes de ler todos os cartões do cliente " + clientName);
                        break;
                    }

                    // [01-01]IDENTIFICADOR DA LINHA   [02-07]NUMERAÇÃO NO LOTE   [08-26]NÚMERO DE CARTAO COMPLETO
                    String lineId = linhaDetalhe.substring(0, 1).trim();
                    String numeroLote = linhaDetalhe.substring(1, 7).trim();
                    String cartaoNumero = linhaDetalhe.substring(7).trim();

                    card.setCardNumber(cartaoNumero);
                    cards.add(card);
                }

                client.setCards(cards);
                clients.add(client);

                // 3. LÊ O RESUMO (Trailer)
                // Após ler os N detalhes, a próxima linha obrigatoriamente é o resumo
                String linhaResumo = reader.readLine();

                if (linhaResumo != null) {
                    // Extrai os dados do resumo (Exemplo: posições 0-10 Totalizadores)
                    String infoResumo = linhaResumo.substring(0, 10).trim();

                    // TODO: Sua lógica para validar ou salvar o resumo
                    System.out.println("Resumo do bloco processado: " + infoResumo);
                }
            }

            save(clients);

            System.out.println("Processamento assíncrono finalizado com sucesso.");

        } catch (IOException e) {
            System.err.println("Erro crítico ao ler o arquivo TXT: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro de formatação posicional (não foi possível converter para número): " + e.getMessage());
        }
    }

    private void gravarCartaoExistente(String nome, String numero) {
        // Sua lógica atual do banco de dados...
    }

}
