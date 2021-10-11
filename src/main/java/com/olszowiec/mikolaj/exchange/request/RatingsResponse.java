package com.olszowiec.mikolaj.exchange.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class RatingsResponse {

    private String source;
    private Map<String, Float> rates;

}