package com.hyperativa.card.repository;

import com.hyperativa.card.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    boolean existsByCardNumber(String cardNumber);

}
