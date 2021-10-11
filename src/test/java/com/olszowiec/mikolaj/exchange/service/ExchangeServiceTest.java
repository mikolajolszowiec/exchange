package com.olszowiec.mikolaj.exchange.service;

import com.olszowiec.mikolaj.exchange.connector.ExchangeConnector;
import com.olszowiec.mikolaj.exchange.dto.ExchangedDto;
import com.olszowiec.mikolaj.exchange.entity.CurrencyEntity;
import com.olszowiec.mikolaj.exchange.entity.ExchangeRateEntity;
import com.olszowiec.mikolaj.exchange.request.ExchangeRequest;
import com.olszowiec.mikolaj.exchange.request.RatingsResponse;
import com.olszowiec.mikolaj.exchange.response.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;

public class ExchangeServiceTest {

    @Mock
    private ExchangeConnector exchangeConnectorMock;
    private ExchangeService exchangeService;

    @BeforeEach
    public void setMock(){
        exchangeService = new ExchangeService(exchangeConnectorMock);
    }

    @org.junit.Test
    public void shouldGetOneCurrencyRating() {
        //given
        exchangeConnectorMock = Mockito.mock(ExchangeConnector.class);
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("BTC")).thenReturn(getExchangeRateEntity("BTC"));
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("ETH")).thenReturn(getExchangeRateEntity("ETH"));
        exchangeService = new ExchangeService(exchangeConnectorMock);
        Float ethBtcExchange = currenciesMap.get("ETH") / currenciesMap.get("BTC");
        //when
        RatingsResponse ratingsResponse = exchangeService.getCurrenciesRatings("BTC", new String[]{"ETH"});
        //then
        assertEquals(ratingsResponse.getSource(),"BTC");
        assertEquals(ratingsResponse.getRates().size(),1);
        assertEquals(ratingsResponse.getRates().get("ETH"), ethBtcExchange);
    }

    @org.junit.Test
    public void shouldGetTwoCurrencyRating() {
        //given
        exchangeConnectorMock = Mockito.mock(ExchangeConnector.class);
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("BTC")).thenReturn(getExchangeRateEntity("BTC"));
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("SHIB,ETH")).thenReturn(getExchangeRateEntity("SHIB","ETH"));
        exchangeService = new ExchangeService(exchangeConnectorMock);
        Float ethBtcExchange = currenciesMap.get("ETH") / currenciesMap.get("BTC");
        Float shibBtcExchange = currenciesMap.get("SHIB") / currenciesMap.get("BTC");
        //when
        RatingsResponse ratingsResponse = exchangeService.getCurrenciesRatings("BTC", new String[]{"SHIB","ETH"});
        //then
        assertEquals(ratingsResponse.getSource(),"BTC");
        assertEquals(ratingsResponse.getRates().size(),2);
        assertEquals(ratingsResponse.getRates().get("ETH"), ethBtcExchange);
        assertEquals(ratingsResponse.getRates().get("SHIB"), shibBtcExchange);
    }

    @org.junit.Test
    public void shouldExchangeOneCurrency() {
        //given
        exchangeConnectorMock = Mockito.mock(ExchangeConnector.class);
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("BTC")).thenReturn(getExchangeRateEntity("BTC"));
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("ETH")).thenReturn(getExchangeRateEntity("ETH"));
        exchangeService = new ExchangeService(exchangeConnectorMock);
        ExchangeRequest exchangeRequest = getExchangeRequest("BTC",121,"ETH");
        //when
        ExchangeResponse exchangeResponse = exchangeService.exchangeCurrency(exchangeRequest);
        ExchangedDto eth = (ExchangedDto) exchangeResponse.getDetails().get("ETH");
        //then
        assertEquals(exchangeResponse.getDetails().get("from"),"BTC");
        assertEquals(exchangeResponse.getDetails().size(),2);
        assertEquals(eth.amount, Integer.valueOf(121));
        assertEquals(eth.rate, Float.valueOf(0.061689965f));
        assertEquals(eth.result, Float.valueOf(7.465232f));
        assertEquals(eth.fee, Float.valueOf(0.0001f));
    }

    @org.junit.Test
    public void shouldExchangeTwoCurrency() {
        //given
        exchangeConnectorMock = Mockito.mock(ExchangeConnector.class);
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("BTC")).thenReturn(getExchangeRateEntity("BTC"));
        Mockito.when(exchangeConnectorMock.getCurrenciesRatings("SHIB,ETH")).thenReturn(getExchangeRateEntity("SHIB","ETH"));
        exchangeService = new ExchangeService(exchangeConnectorMock);
        Float ethBtcExchange = currenciesMap.get("ETH") / currenciesMap.get("BTC");
        ExchangeRequest exchangeRequest = getExchangeRequest("BTC",121,"SHIB","ETH");
        //when
        ExchangeResponse exchangeResponse = exchangeService.exchangeCurrency(exchangeRequest);
        ExchangedDto shib = (ExchangedDto) exchangeResponse.getDetails().get("SHIB");
        ExchangedDto eth = (ExchangedDto) exchangeResponse.getDetails().get("ETH");
        //then
        assertEquals(exchangeResponse.getDetails().get("from"),"BTC");
        assertEquals(exchangeResponse.getDetails().size(),3);
        assertEquals(shib.amount, Integer.valueOf(121));
        assertEquals(shib.rate, Float.valueOf(5.5380633E-10f));
        assertEquals(shib.result, Float.valueOf(6.701727E-8f));
        assertEquals(shib.fee, Float.valueOf(1.0E-4f));
        assertEquals(eth.amount, Integer.valueOf(121));
        assertEquals(eth.rate, Float.valueOf(0.061689965f));
        assertEquals(eth.result, Float.valueOf(7.465232f));
        assertEquals(eth.fee, Float.valueOf(0.0001f));
    }

    private ExchangeRequest getExchangeRequest(String from, Integer amount, String...to){
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setFrom(from);
        exchangeRequest.setAmount(amount);
        exchangeRequest.setTo(Arrays.asList(to));
        return exchangeRequest;
    }

    private ExchangeRateEntity getExchangeRateEntity(String...symbols){
        ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();
        exchangeRateEntity.data = new LinkedList<>();
        for (String symbol: symbols){
            exchangeRateEntity.data.add(getCurrencyEntity(symbol));
        }
        return exchangeRateEntity;
    }

    private CurrencyEntity getCurrencyEntity(final String symbol){
        CurrencyEntity currencyEntity = new CurrencyEntity();
        currencyEntity.symbol = symbol;
        currencyEntity.price = currenciesMap.get(symbol);
        return currencyEntity;
    }

    Map<String, Float> currenciesMap = Stream.of(
            new AbstractMap.SimpleEntry<>("BTC", 57619.42f),
            new AbstractMap.SimpleEntry<>("ETH", 3554.54f),
            new AbstractMap.SimpleEntry<>("SHIB", 0.00003191f),
            new AbstractMap.SimpleEntry<>("XPR", 0.02205239f))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}