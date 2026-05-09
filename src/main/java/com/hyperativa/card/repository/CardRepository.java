package com.hyperativa.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hyperativa.card.model.Card;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    // O Spring traduz isso para: SELECT * FROM cards WHERE client_name = ?
    List<Card> findByClientName(String clientName);

}
