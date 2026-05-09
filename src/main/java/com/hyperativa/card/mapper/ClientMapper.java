package com.hyperativa.card.mapper;

import com.hyperativa.card.dto.Client;
import com.hyperativa.card.model.ClientEntity;
import org.mapstruct.Mapper;

// A propriedade "uses" diz ao MapStruct como converter as listas aninhadas
@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface ClientMapper {

    // Transforma a Entity no DTO (incluindo a lista de cartões)
    Client toDto(ClientEntity entity);

    // Transforma o DTO na Entity
    ClientEntity toEntity(Client dto);
}
