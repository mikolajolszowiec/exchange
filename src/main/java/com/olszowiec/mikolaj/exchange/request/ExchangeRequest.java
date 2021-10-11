package com.olszowiec.mikolaj.exchange.request;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeRequest {

    private String from;
    private List<String> to;
    private Integer amount;

}