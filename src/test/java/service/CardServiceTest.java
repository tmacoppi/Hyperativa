package service;

import com.hyperativa.card.dto.Card;
import com.hyperativa.card.dto.Client;
import com.hyperativa.card.mapper.CardMapper;
import com.hyperativa.card.mapper.ClientMapper;
import com.hyperativa.card.model.CardEntity;
import com.hyperativa.card.model.ClientEntity;
import com.hyperativa.card.repository.CardRepository;
import com.hyperativa.card.repository.ClientRepository;
import com.hyperativa.card.repository.ImportRepository;
import com.hyperativa.card.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ImportRepository importRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void getCardsByClient_WhenClientExists_ShouldReturnClientWithCards() {
        String clientName = "John Doe";

        ClientEntity clientEntity = createClientEntity();
        CardEntity cardEntity = createCardEntity();

        Client clientDto = createClientDto();
        Card cardDto = createCardDto();

        when(clientRepository.findByName(clientName)).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDto(clientEntity)).thenReturn(clientDto);
        when(cardRepository.findAllByIdClient(clientEntity.getId())).thenReturn(List.of(cardEntity));
        when(cardMapper.toDto(cardEntity)).thenReturn(cardDto);

        List<Client> result = cardService.getCardsByClient(clientName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
        assertEquals(1, result.getFirst().getCards().size());
        assertEquals("1234567890123456", result.getFirst().getCards().getFirst().getCardNumber());

        verify(clientRepository).findByName(clientName);
        verify(clientMapper).toDto(clientEntity);
        verify(cardRepository).findAllByIdClient(clientEntity.getId());
        verify(cardMapper).toDto(cardEntity);
    }

    @Test
    void getCardsByClient_WhenClientDoesNotExist_ShouldThrowException() {
        String clientName = "Unknown";

        when(clientRepository.findByName(clientName)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cardService.getCardsByClient(clientName)
        );

        assertEquals("Cliente [Unknown] não encontrado.", exception.getMessage());

        verify(clientRepository).findByName(clientName);
        verifyNoInteractions(cardRepository, clientMapper, cardMapper);
    }

    @Test
    void getCardByCardNumber_WhenCardExists_ShouldReturnClientWithCard() {
        String cardNumber = "1234567890123456";

        CardEntity cardEntity = createCardEntity();
        ClientEntity clientEntity = createClientEntity();

        Client clientDto = createClientDto();
        Card cardDto = createCardDto();

        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(cardEntity));
        when(clientRepository.findById(cardEntity.getIdClient())).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDto(clientEntity)).thenReturn(clientDto);
        when(cardMapper.toDto(cardEntity)).thenReturn(cardDto);

        List<Client> result = cardService.getCardByCardNumber(cardNumber);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
        assertEquals(1, result.getFirst().getCards().size());
        assertEquals(cardNumber, result.getFirst().getCards().getFirst().getCardNumber());

        verify(cardRepository).findByCardNumber(cardNumber);
        verify(clientRepository).findById(cardEntity.getIdClient());
        verify(clientMapper).toDto(clientEntity);
        verify(cardMapper).toDto(cardEntity);
    }

    @Test
    void getCardByCardNumber_WhenCardDoesNotExist_ShouldThrowException() {
        String cardNumber = "9999999999999999";

        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cardService.getCardByCardNumber(cardNumber)
        );

        assertEquals("Cartão não encontrado", exception.getMessage());

        verify(cardRepository).findByCardNumber(cardNumber);
        verifyNoInteractions(clientRepository, clientMapper, cardMapper);
    }

    @Test
    void getCardByCardNumber_WhenClientDoesNotExist_ShouldThrowException() {
        String cardNumber = "1234567890123456";
        CardEntity cardEntity = createCardEntity();

        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(cardEntity));
        when(clientRepository.findById(cardEntity.getIdClient())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cardService.getCardByCardNumber(cardNumber)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());

        verify(cardRepository).findByCardNumber(cardNumber);
        verify(clientRepository).findById(cardEntity.getIdClient());
        verifyNoInteractions(clientMapper, cardMapper);
    }

    @Test
    void saveByClientNameAndCardNumber_WhenClientExists_ShouldSaveCard() {
        String clientName = "John Doe";
        String cardNumber = "1234567890123456";

        ClientEntity clientEntity = createClientEntity();
        Client clientDto = createClientDto();

        when(clientRepository.findByName(clientName)).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDto(clientEntity)).thenReturn(clientDto);
        when(cardMapper.toDto(any(CardEntity.class))).thenReturn(createCardDto());

        List<Client> result = cardService.save(clientName, cardNumber);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
        assertEquals(1, result.getFirst().getCards().size());

        verify(clientRepository).findByName(clientName);
        verify(clientRepository, never()).save(any(ClientEntity.class));
        verify(cardRepository).save(any(CardEntity.class));
        verify(clientMapper).toDto(clientEntity);
        verify(cardMapper).toDto(any(CardEntity.class));
    }

    @Test
    void saveByClientNameAndCardNumber_WhenClientDoesNotExist_ShouldCreateClientAndSaveCard() {
        String clientName = "Jane Doe";
        String cardNumber = "9876543210987654";

        ClientEntity savedClientEntity = new ClientEntity();
        savedClientEntity.setId(2L);
        savedClientEntity.setName(clientName);

        Client clientDto = new Client();
        clientDto.setId(2);
        clientDto.setName(clientName);

        when(clientRepository.findByName(clientName)).thenReturn(Optional.empty());
        when(clientRepository.save(any(ClientEntity.class))).thenAnswer(invocation -> {
            ClientEntity entity = invocation.getArgument(0);
            entity.setId(2L);
            return entity;
        });
        when(clientMapper.toDto(any(ClientEntity.class))).thenReturn(clientDto);
        when(cardMapper.toDto(any(CardEntity.class))).thenReturn(createCardDto());

        List<Client> result = cardService.save(clientName, cardNumber);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clientName, result.getFirst().getName());

        verify(clientRepository).findByName(clientName);
        verify(clientRepository).save(any(ClientEntity.class));
        verify(cardRepository).save(any(CardEntity.class));
        verify(clientMapper).toDto(any(ClientEntity.class));
        verify(cardMapper).toDto(any(CardEntity.class));
    }

    @Test
    void saveClientList_WhenClientDoesNotExistAndCardDoesNotExist_ShouldCreateClientAndCards() {
        Client inputClient = createClientDto();
        Card inputCard = createCardDto();
        inputClient.setCards(List.of(inputCard));

        ClientEntity clientEntity = createClientEntity();
        CardEntity cardEntity = createCardEntity();

        when(clientRepository.findByName(inputClient.getName())).thenReturn(Optional.empty());
        when(clientMapper.toEntity(inputClient)).thenReturn(clientEntity);
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);
        when(clientMapper.toDto(clientEntity)).thenReturn(createClientDto());
        when(cardMapper.toEntity(inputCard)).thenReturn(cardEntity);
        when(cardRepository.existsByCardNumber(cardEntity.getCardNumber())).thenReturn(false);
        when(cardRepository.saveAll(anyList())).thenReturn(List.of(cardEntity));
        when(cardRepository.findAllByIdClient(clientEntity.getId())).thenReturn(List.of(cardEntity));
        when(cardMapper.toDto(List.of(cardEntity))).thenReturn(List.of(createCardDto()));

        List<Client> result = cardService.save(List.of(inputClient));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().getName());
        assertEquals(1, result.getFirst().getCards().size());

        verify(clientRepository).findByName(inputClient.getName());
        verify(clientMapper).toEntity(inputClient);
        verify(clientRepository).save(clientEntity);
        verify(cardRepository).existsByCardNumber(cardEntity.getCardNumber());
        verify(cardRepository).saveAll(anyList());
        verify(cardRepository).findAllByIdClient(clientEntity.getId());
    }

    @Test
    void saveClientList_WhenClientExistsAndCardAlreadyExists_ShouldNotSaveDuplicatedCard() {
        Client inputClient = createClientDto();
        Card inputCard = createCardDto();
        inputClient.setCards(List.of(inputCard));

        ClientEntity clientEntity = createClientEntity();
        CardEntity cardEntity = createCardEntity();

        when(clientRepository.findByName(inputClient.getName())).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDto(clientEntity)).thenReturn(createClientDto());
        when(cardMapper.toEntity(inputCard)).thenReturn(cardEntity);
        when(cardRepository.existsByCardNumber(cardEntity.getCardNumber())).thenReturn(true);
        when(cardRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(cardRepository.findAllByIdClient(clientEntity.getId())).thenReturn(List.of(cardEntity));
        when(cardMapper.toDto(List.of(cardEntity))).thenReturn(List.of(createCardDto()));

        List<Client> result = cardService.save(List.of(inputClient));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getCards().size());

        verify(clientRepository).findByName(inputClient.getName());
        verify(clientRepository, never()).save(any(ClientEntity.class));
        verify(cardRepository).existsByCardNumber(cardEntity.getCardNumber());
        //verify(cardRepository).saveAll(result.getFirst().getCards().size() > 0);
        verify(cardRepository).findAllByIdClient(clientEntity.getId());
    }

    private ClientEntity createClientEntity() {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        clientEntity.setName("John Doe");
        return clientEntity;
    }

    private CardEntity createCardEntity() {
        CardEntity cardEntity = new CardEntity();
        cardEntity.setId(1L);
        cardEntity.setIdClient(1L);
        cardEntity.setIdImport(1L);
        cardEntity.setCardNumber("1234567890123456");
        return cardEntity;
    }

    private Client createClientDto() {
        Client client = new Client();
        client.setId(1);
        client.setName("John Doe");
        client.setCards(new ArrayList<>());
        return client;
    }

    private Card createCardDto() {
        Card card = new Card();
        card.setId(1);
        card.setIdClient(1);
        card.setIdImport(1);
        card.setCardNumber("1234567890123456");
        return card;
    }
}
