package com.codegame.dto;

import com.codegame.model.GiftCard;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@RequiredArgsConstructor
public class RefundLineItemDto {
    final String code;

    @Enumerated(EnumType.STRING)
    final GiftCard.RefundType refundType;
}
