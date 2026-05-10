package com.hyperativa.card.repository;

import com.hyperativa.card.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    boolean existsByCardNumber(String cardNumber);

    List<CardEntity> findAllByIdClient(Long id);

    Optional<CardEntity> findByCardNumber(String cardNumber);
}
