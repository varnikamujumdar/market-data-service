package com.example.marketdataservice.service;

import com.example.marketdataservice.dto.*;
import com.example.marketdataservice.models.PollingJobConfig;
import com.example.marketdataservice.models.PricePoint;
import com.example.marketdataservice.models.RawMarketData;
import com.example.marketdataservice.provider.MarketDataProvider;
import com.example.marketdataservice.repository.PollingJobConfigRepository;
import com.example.marketdataservice.repository.PricePointRepository;
import com.example.marketdataservice.repository.RawMarketDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PriceService {

    private final ObjectMapper objectMapper;

    @Autowired
    private final PollingJobConfigRepository pollingJobConfigRepository;

    @Autowired
    private final RawMarketDataRepository rawMarketDataRepository;

    private final MarketDataProvider marketDataProvider;

    @Autowired
    private final PricePointRepository pricePointRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PriceService(ObjectMapper objectMapper, PollingJobConfigRepository pollingJobConfigRepository, RawMarketDataRepository rawMarketDataRepository, MarketDataProvider marketDataProvider, PricePointRepository pricePointRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.pollingJobConfigRepository = pollingJobConfigRepository;
        this.rawMarketDataRepository = rawMarketDataRepository;
        this.marketDataProvider = marketDataProvider;
        this.pricePointRepository = pricePointRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public PriceResponse getLatestPrice(String symbol, String provider) {
        try {
            PriceResponse priceResponse = marketDataProvider.fetchPrice(symbol);

            RawMarketData rawMarketData = RawMarketData.builder()
                    .symbol(symbol)
                    .timestamp(priceResponse.getTimeStamp())
                    .provider(provider)
                    .rawResponse("STUB")
                    .build();

            rawMarketDataRepository.save(rawMarketData);

            PricePoint pricePoint = PricePoint.builder()
                    .price(priceResponse.getPrice())
                    .timestamp(priceResponse.getTimeStamp())
                    .provider(provider)
                    .symbol(symbol)
                    .rawMarketData(rawMarketData)
                    .build();

            pricePointRepository.save(pricePoint);

            PriceEvent event = PriceEvent.builder()
                    .symbol(symbol)
                    .price(priceResponse.getPrice())
                    .timestamp(priceResponse.getTimeStamp())
                    .source(provider)
                    .rawResponseId(rawMarketData.getId().toString())
                    .build();

            kafkaTemplate.send("price-events", event);
            log.info("Published price event to Kafka for symbol: {}", symbol);
            return priceResponse;

        } catch (Exception e) {
            log.error("An exception occurred while fetching price for symbol " + symbol, e);
            throw new RuntimeException("Error fetching price: " + e.getMessage());
        }
    }

    public PollResponse startPolling(PollRequest pollRequest) throws JsonProcessingException {

        log.info("Processing polling request");
        String jobId = "poll_" + UUID.randomUUID().toString();

        PollingJobConfig pollingJobConfig = PollingJobConfig.builder()
                .jobId(jobId)
                .status("accepted")
                .provider(pollRequest.getProvider())
                .interval(pollRequest.getInterval())
                .createdAt(ZonedDateTime.now())
                .symbolsJson(objectMapper.writeValueAsString(pollRequest.getSymbols()))
                .build();
        pollingJobConfigRepository.save(pollingJobConfig);

        log.info("Saved Poll Request details as PollJobConfig in database");

        log.info("Preparing Polling response");

        PollResponse pollResponse = PollResponse.builder()
                .jobId(jobId)
                .status("accepted")
                .config(ConfigDTO.builder()
                        .symbols(pollRequest.getSymbols())
                        .interval(pollRequest.getInterval())
                        .build())
                .build();

        log.info("Polling response prepared successfully and sending to user");

        return pollResponse;
    }
}
