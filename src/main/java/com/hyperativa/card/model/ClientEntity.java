package com.hyperativa.card.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "client") // Nome da tabela que será criada/buscada no MySQL
@Getter
@Setter
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;
}
