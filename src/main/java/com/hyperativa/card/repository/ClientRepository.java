package com.hyperativa.card.repository;

import com.hyperativa.card.model.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByName(String name);

    ClientEntity findByName(String name);

}
