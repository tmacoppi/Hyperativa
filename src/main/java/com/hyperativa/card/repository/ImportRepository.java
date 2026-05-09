package com.hyperativa.card.repository;

import com.hyperativa.card.model.ImportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportRepository  extends JpaRepository<ImportEntity, Long> {
}
