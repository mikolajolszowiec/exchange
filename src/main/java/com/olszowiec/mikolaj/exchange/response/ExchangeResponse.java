package com.olszowiec.mikolaj.exchange.response;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ExchangeResponse {

    Map<String, Object> details = new LinkedHashMap<>();

    @JsonAnySetter
    public void setDetail(String key, Object value) {
        details.put(key, value);
    }

}