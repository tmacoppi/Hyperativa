package com.hyperativa.card.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "import")
@Getter
@Setter
public class ImportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String chunk;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "client_id", nullable = false)
    private Long idClient;

}
