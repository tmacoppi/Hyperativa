package com.hyperativa.card.mapper;

import com.hyperativa.card.dto.Card;
import com.hyperativa.card.model.CardEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card toDto(CardEntity entity);

    CardEntity toEntity(Card dto);
}
