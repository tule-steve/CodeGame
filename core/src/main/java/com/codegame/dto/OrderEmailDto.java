package com.codegame.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
public class OrderEmailDto {
    Long itemId;

    String description;

    Integer price;

    BigInteger count;

    String codes;


}
