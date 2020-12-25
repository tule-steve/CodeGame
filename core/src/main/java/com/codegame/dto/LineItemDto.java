package com.codegame.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class LineItemDto {
    final Long itemId;

    final String description;

    final Integer amount;

    final Integer price;
}
