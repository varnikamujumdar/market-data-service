package com.example.marketdataservice.provider;

import com.example.marketdataservice.schemas.PriceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Map;

@Component
public class AlphaVantageProvider implements MarketDataProvider {

    @Value("${alpha.api.key}")
    private String apiKey;

    private RestTemplate restTemplate = new RestTemplate();


//    public AlphaVantageProvider(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }


    @Override
    public PriceResponse fetchPrice(String symbol) throws Exception {
        // call the Aplha Vantage API that fetches the latest price based on the Symbol

        String alphaVantageUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY"
                + "&symbol=" + symbol
                + "&interval=1min"
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(alphaVantageUrl, Map.class);

        if (response == null || !response.containsKey("Time Series (1min)")) {
            throw new Exception("Invalid response from Alpha Vantage");
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (1min)");
        String latestTimestamp = timeSeries.keySet().iterator().next(); // get latest time
        Map<String, String> latestData = (Map<String, String>) timeSeries.get(latestTimestamp);

        double price = Double.parseDouble(latestData.get("4. close"));

        return PriceResponse.builder()
                .symbol(symbol)
                .price(price)
                .timeStamp(ZonedDateTime.parse(latestTimestamp.replace(" ", "T") + "Z"))
                .provider("alpha_vantage")
                .build();
    }
}
