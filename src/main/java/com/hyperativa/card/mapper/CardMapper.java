package com.hyperativa.card.mapper;

import com.hyperativa.card.dto.Card;
import com.hyperativa.card.model.CardEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card toDto(CardEntity entity);

    CardEntity toEntity(Card dto);

    List<Card> toDto(List<CardEntity> entity);

    List<CardEntity> toEntity(List<Card> dto);
}
