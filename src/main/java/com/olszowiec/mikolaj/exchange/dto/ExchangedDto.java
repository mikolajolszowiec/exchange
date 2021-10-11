package com.olszowiec.mikolaj.exchange.dto;

import lombok.Builder;

@Builder
public class ExchangedDto {
    public Float rate;
    public Integer amount;
    public Float result;
    public Float fee;
}
