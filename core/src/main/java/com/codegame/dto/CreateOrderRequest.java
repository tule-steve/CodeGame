package com.codegame.dto;

import com.codegame.security.config.CustomOAuth2RequestFactory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateOrderRequest {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderRequest.class);

    @NotNull
    final Long orderId;

    @Min(1)
    final Long transactionTotal;

    @NotBlank(message = "email id is required.")
    final String email;

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
