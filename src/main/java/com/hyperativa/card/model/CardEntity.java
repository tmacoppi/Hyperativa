package com.hyperativa.card.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;

}
