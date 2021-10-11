package com.olszowiec.mikolaj.exchange.controller;

import com.olszowiec.mikolaj.exchange.request.ExchangeRequest;
import com.olszowiec.mikolaj.exchange.request.RatingsResponse;
import com.olszowiec.mikolaj.exchange.response.ExchangeResponse;
import com.olszowiec.mikolaj.exchange.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    /**
     * @param currency - string for currency symbol
     * @param filter - optional variable
     * @return list of exchange ratings for given currency
     */
    @lombok.SneakyThrows
    @GetMapping("/currencies/{currency}")
    public RatingsResponse getCurrenciesRatings(@PathVariable String currency, @RequestParam(required = false) String[] filter) {
        return exchangeService.getCurrenciesRatings(currency, filter);
    }

    /**
     * @param exchangeRequest - object containing base and target currencies symbols and amount
     * @return list of exchanged currencies
     */
    @lombok.SneakyThrows
    @PostMapping("/currencies/exchange")
    public ExchangeResponse exchangeCurrency(@RequestBody ExchangeRequest exchangeRequest) {
        return exchangeService.exchangeCurrency(exchangeRequest);
    }

}
