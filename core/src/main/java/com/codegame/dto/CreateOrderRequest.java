package com.codegame.dto;

import com.codegame.security.config.CustomOAuth2RequestFactory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CreateOrderRequest {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderRequest.class);

    final Long orderId;

    final Long transaction_total;

    final List<LineItemDto> lineItems;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            logger.error("Error on parsing the order detail data to string", ex);
        }
        return null;
    }
}
