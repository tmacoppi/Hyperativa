package com.hyperativa.card.repository;

import com.hyperativa.card.model.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByName(String name);

    Optional<ClientEntity> findByName(String name);

}
