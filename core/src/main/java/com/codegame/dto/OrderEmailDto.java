package com.codegame.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class OrderEmailDto {
    final Integer itemId;

    final String description;

    final Integer price;

    final BigInteger count;

    final String codes;


}
