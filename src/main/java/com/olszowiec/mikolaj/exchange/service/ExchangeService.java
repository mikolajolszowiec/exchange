package com.olszowiec.mikolaj.exchange.service;

import com.olszowiec.mikolaj.exchange.connector.ExchangeConnector;
import com.olszowiec.mikolaj.exchange.dto.ExchangedDto;
import com.olszowiec.mikolaj.exchange.entity.CurrencyEntity;
import com.olszowiec.mikolaj.exchange.entity.ExchangeRateEntity;
import com.olszowiec.mikolaj.exchange.request.ExchangeRequest;
import com.olszowiec.mikolaj.exchange.request.RatingsResponse;
import com.olszowiec.mikolaj.exchange.response.ExchangeResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

    private final ExchangeConnector exchangeConnector;

    public ExchangeService(ExchangeConnector exchangeConnector) {
        this.exchangeConnector = exchangeConnector;
    }

    public RatingsResponse getCurrenciesRatings(String currency, String[] filters) {
        Map<String, Float> ratesMap = loadRatings(currency, filters);
        RatingsResponse ratingsResponse = RatingsResponse.builder()
                .source(currency)
                .rates(ratesMap)
                .build();
        return ratingsResponse;
    }

    public ExchangeResponse exchangeCurrency(final ExchangeRequest exchangeRequest) {
        Map<String, ExchangedDto> dtoMap = getCalculatedRatings(exchangeRequest);
        ExchangeResponse exchangeResponse = new ExchangeResponse();
        exchangeResponse.setDetail("from", exchangeRequest.getFrom());
        dtoMap.entrySet().forEach(x -> exchangeResponse.setDetail(x.getKey(), x.getValue()));
        return exchangeResponse;
    }

    private Map<String, ExchangedDto> getCalculatedRatings(ExchangeRequest exchangeRequest) {
        String[] filters = exchangeRequest.getTo().stream().toArray(String[]::new);
        return loadRatings(trimWord(exchangeRequest.getFrom()), filters).entrySet().stream()
                .collect(Collectors.toMap(d -> d.getKey(), d -> buildExchangedDto(exchangeRequest.getAmount(), d.getValue())));
    }

    private Map<String, Float> loadRatings(String currency, String[] filters) {
        final ExchangeRateEntity baseExchangeRate = exchangeConnector.getCurrenciesRatings(currency);
        Map<String, Float> ratingsMap = new HashMap<>();
        if (filters != null && filters.length > 0 && baseExchangeRate.data.stream().findFirst().isPresent()) {
            String currencyString = Arrays.stream(filters).collect(Collectors.joining(","));
            ExchangeRateEntity rating = exchangeConnector.getCurrenciesRatings(currencyString);
            final CurrencyEntity currencyEntity = baseExchangeRate.data.stream().findFirst().get();
            ratingsMap = rating.data.stream().collect(Collectors.toMap(d -> d.symbol, d -> calcExchange(currencyEntity, d)));
        }
        return ratingsMap;
    }

    private Float calcExchange(final CurrencyEntity baseCurrency, final CurrencyEntity targetCurrency) {
        if (baseCurrency == null || targetCurrency == null) {
            return 0.0f;
        }
        return targetCurrency.price / baseCurrency.price;
    }

    private String trimWord(final String word) {
        return word.replace("'", "");
    }

    private ExchangedDto buildExchangedDto(final Integer amount, final Float rate) {
        float fee = 0.0001f;
        return ExchangedDto.builder()
                .amount(amount)
                .rate(rate)
                .result(amount * rate + fee * amount * rate)
                .fee(fee)
                .build();
    }
}