package controller;

import com.hyperativa.card.controller.CardController;
import com.hyperativa.card.dto.Card;
import com.hyperativa.card.dto.CardRequest;
import com.hyperativa.card.dto.CardResponse;
import com.hyperativa.card.dto.Client;
import com.hyperativa.card.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @Test
    void getCardsByClient_ShouldReturnOkWithClients() {
        String clientName = "John Doe";
        List<Client> clients = List.of(createClient());

        when(cardService.getCardsByClient(clientName)).thenReturn(clients);

        ResponseEntity<CardResponse> response = cardController.getCardsByClient(clientName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(clients, response.getBody().getClients());

        verify(cardService).getCardsByClient(clientName);
        verifyNoMoreInteractions(cardService);
    }

    @Test
    void registerCardForClient_ShouldReturnOkWithClients() {
        String clientName = "John Doe";
        String cardNumber = "1234567890123456";
        List<Client> clients = List.of(createClient());

        when(cardService.save(clientName, cardNumber)).thenReturn(clients);

        ResponseEntity<CardResponse> response = cardController.registerCardForClient(clientName, cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(clients, response.getBody().getClients());

        verify(cardService).save(clientName, cardNumber);
        verifyNoMoreInteractions(cardService);
    }

    @Test
    void registerCard_ShouldReturnOkWithClients() {
        Client client = createClient();

        CardRequest request = new CardRequest();
        request.setClients(List.of(client));

        List<Client> savedClients = List.of(client);

        when(cardService.save(request.getClients())).thenReturn(savedClients);

        ResponseEntity<CardResponse> response = cardController.registerCard(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedClients, response.getBody().getClients());

        verify(cardService).save(request.getClients());
        verifyNoMoreInteractions(cardService);
    }

    @Test
    void getCardByCardNumber_ShouldReturnOkWithClients() {
        String cardNumber = "1234567890123456";
        List<Client> clients = List.of(createClient());

        when(cardService.getCardByCardNumber(cardNumber)).thenReturn(clients);

        ResponseEntity<CardResponse> response = cardController.getCardByCardNumber(cardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(clients, response.getBody().getClients());

        verify(cardService).getCardByCardNumber(cardNumber);
        verifyNoMoreInteractions(cardService);
    }

    @Test
    void importCards_ShouldReturnAccepted() {
        doNothing().when(cardService).importCardsFromFile();

        ResponseEntity<Void> response = cardController.importCards();

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNull(response.getBody());

        verify(cardService).importCardsFromFile();
        verifyNoMoreInteractions(cardService);
    }

    private Client createClient() {
        Card card = new Card();
        card.setId(1);
        card.setIdClient(1);
        card.setCardNumber("1234567890123456");

        Client client = new Client();
        client.setId(1);
        client.setName("John Doe");
        client.setCards(List.of(card));

        return client;
    }
}
