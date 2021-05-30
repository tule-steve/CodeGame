package com.codegame.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ChangeStatusItemRequest {

    @NotNull(message = "item id is required")
    Long itemId;

    Boolean isUnpublished = false;
}
