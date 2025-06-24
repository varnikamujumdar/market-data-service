package com.example.marketdataservice.provider;

import com.example.marketdataservice.dto.PriceResponse;


public interface MarketDataProvider {

    PriceResponse fetchPrice(String symbol) throws Exception;
}
