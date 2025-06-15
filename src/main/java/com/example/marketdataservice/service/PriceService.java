package com.example.marketdataservice.service;

import com.example.marketdataservice.schemas.ConfigDTO;
import com.example.marketdataservice.schemas.PollRequest;
import com.example.marketdataservice.schemas.PollResponse;
import com.example.marketdataservice.schemas.PriceResponse;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class PriceService {

    public PriceResponse getLatestPrice(String symbol, String provider) {
        if (provider.isEmpty() || provider.equals(null)) {
            provider = "alpha-vantage";
        }
        if(symbol.isEmpty() || symbol.equals(null)) {
            symbol="AAPL";
        }
        PriceResponse priceResponse = PriceResponse.builder()
                .symbol(symbol)
                .price(150.25)
                .timeStamp(ZonedDateTime.now())
                .provider(provider)
                .build();
        return priceResponse;
    }

    public PollResponse startPolling(PollRequest pollRequest) {
        PollResponse pollResponse = PollResponse.builder()
                .jobId("poll_" + UUID.randomUUID().toString())
                .status("accepted")
                .config(ConfigDTO.builder()
                        .symbols(pollRequest.getSymbols())
                        .interval(pollRequest.getInterval())
                        .build())
                .build();

        return pollResponse;
    }
}
