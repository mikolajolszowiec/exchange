package com.olszowiec.mikolaj.exchange.connector;

import com.olszowiec.mikolaj.exchange.entity.ExchangeRateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeConnector {

    private final RestTemplate restTemplate;
    @Value("${api.url}")
    private String API_URL;
    @Value("${api.key}")
    private String API_KEY;

    public ExchangeConnector(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ExchangeRateEntity getCurrenciesRatings(String currency) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        Map<String, String> params = new HashMap<String, String>();
        params.put("data", "assets");
        params.put("key", API_KEY);
        params.put("symbol", currency);

        HttpEntity entity = new HttpEntity(headers);
        HttpEntity<ExchangeRateEntity> response = this.restTemplate.exchange(API_URL, HttpMethod.GET, entity, ExchangeRateEntity.class, params);
        return response.getBody();
    }

}