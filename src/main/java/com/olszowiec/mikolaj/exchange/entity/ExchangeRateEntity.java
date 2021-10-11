package com.olszowiec.mikolaj.exchange.entity;

import java.util.List;

public class ExchangeRateEntity {
    public ConfigEntity config;
    public UsageEntity usage;
    public List<CurrencyEntity> data = null;
}
