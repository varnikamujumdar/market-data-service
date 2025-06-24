package com.example.marketdataservice.service;

import com.example.marketdataservice.dto.PriceEvent;
import com.example.marketdataservice.models.PricePoint;
import com.example.marketdataservice.models.SymbolAverage;
import com.example.marketdataservice.repository.PricePointRepository;
import com.example.marketdataservice.repository.SymbolAverageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceEventConsumer {

    private final PricePointRepository pricePointRepository;
    private final SymbolAverageRepository symbolAverageRepository;

    @KafkaListener(topics = "price-events", groupId = "ma-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(PriceEvent event) {
        try {
            log.info("Consumed event for symbol: {}", event.getSymbol());

            // Get the last 4 price points before the current timestamp
            List<PricePoint> last4 = pricePointRepository
                    .findTop4BySymbolAndTimestampLessThanOrderByTimestampDesc(event.getSymbol(), event.getTimestamp());

            if (last4.size() < 4) {
                log.warn("Not enough previous data to compute moving average for {}", event.getSymbol());
                return;
            }

            double total = event.getPrice();
            for (PricePoint p : last4) {
                total += p.getPrice();
            }
            double ma = total / 5.0;

            SymbolAverage average = SymbolAverage.builder()
                    .symbol(event.getSymbol())
                    .timestamp(event.getTimestamp())
                    .movingAverage(ma)
                    .build();

            symbolAverageRepository.save(average);
            log.info("Saved 5-point MA for {} at {}: {}", event.getSymbol(), event.getTimestamp(), ma);

        } catch (Exception e) {
            log.error("Failed to process event: {}", event, e);
        }
    }
}
