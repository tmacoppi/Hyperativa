package com.hyperativa.card.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "card") // Nome da tabela que será criada/buscada no MySQL
@Getter
@Setter
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long idClient;

    @Column(name = "import_id", nullable = false)
    private Long idImport;

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

}
