package com.example.marketdataservice.service;

import com.example.marketdataservice.dto.PriceEvent;
import com.example.marketdataservice.models.PollingJobConfig;
import com.example.marketdataservice.models.PricePoint;
import com.example.marketdataservice.models.RawMarketData;
import com.example.marketdataservice.provider.MarketDataProvider;
import com.example.marketdataservice.repository.PollingJobConfigRepository;
import com.example.marketdataservice.repository.PricePointRepository;
import com.example.marketdataservice.repository.RawMarketDataRepository;
import com.example.marketdataservice.dto.PriceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class PollingSchedulerService {

    private final PollingJobConfigRepository pollingJobConfigRepository;

    private final RawMarketDataRepository rawMarketDataRepository;

    private final PricePointRepository pricePointRepository;

    private final MarketDataProvider marketDataProvider;

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PollingSchedulerService(PollingJobConfigRepository pollingJobConfigRepository, RawMarketDataRepository rawMarketDataRepository, PricePointRepository pricePointRepository, MarketDataProvider marketDataProvider, ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.pollingJobConfigRepository = pollingJobConfigRepository;
        this.rawMarketDataRepository = rawMarketDataRepository;
        this.pricePointRepository = pricePointRepository;
        this.marketDataProvider = marketDataProvider;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public void scheduledTask() {

        List<PollingJobConfig> jobsWithStatusAsAccepted = pollingJobConfigRepository.findByStatus("accepted");
        for (PollingJobConfig job : jobsWithStatusAsAccepted) {
            if (job.getLastExecutedAt() == null) {
                shouldRun(job);
            } else {
                Duration duration = Duration.between(job.getLastExecutedAt(), ZonedDateTime.now());
                if (duration.getSeconds() >= job.getInterval()) {
                    shouldRun(job);
                }
            }
        }

    }

    public void shouldRun(PollingJobConfig job) {
        try {
            List<String> symbols = objectMapper.readValue(job.getSymbolsJson(), new TypeReference<>() {
            });
            for (String symbol : symbols) {
                log.info("Fetching price for symbol: {}", symbol);
                PriceResponse priceResponse = marketDataProvider.fetchPrice(symbol);

                RawMarketData raw = RawMarketData.builder()
                        .symbol(priceResponse.getSymbol())
                        .timestamp(priceResponse.getTimeStamp())
                        .provider(priceResponse.getProvider())
                        .rawResponse(objectMapper.writeValueAsString(priceResponse)) // ideally, actual JSON string from API
                        .build();
                rawMarketDataRepository.save(raw);


                PriceEvent event = PriceEvent.builder()
                        .symbol(priceResponse.getSymbol())
                        .price(priceResponse.getPrice())
                        .timestamp(priceResponse.getTimeStamp())
                        .source(priceResponse.getProvider())
                        .rawResponseId(raw.getId().toString()) // assuming UUID
                        .build();

                kafkaTemplate.send("price-events", event);
                log.info("Published price event to Kafka for symbol: {}", event.getSymbol());

                PricePoint point = PricePoint.builder()
                        .price(priceResponse.getPrice())
                        .timestamp(priceResponse.getTimeStamp())
                        .provider(priceResponse.getProvider())
                        .symbol(priceResponse.getSymbol())
                        .rawMarketData(raw)
                        .build();
                pricePointRepository.save(point);
            }

            job.setLastExecutedAt(ZonedDateTime.now());
            pollingJobConfigRepository.save(job);
        } catch (Exception e) {
            log.error("Job failed: {}", e.getMessage(), e);
            job.setStatus("failed");
            pollingJobConfigRepository.save(job);
        }
    }

}
