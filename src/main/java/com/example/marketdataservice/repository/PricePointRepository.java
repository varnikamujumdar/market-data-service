package com.example.marketdataservice.repository;


import com.example.marketdataservice.models.PricePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PricePointRepository extends JpaRepository<PricePoint, UUID> {

    // Get the last 4 prices before a given timestamp (for MA calculation)
    List<PricePoint> findTop4BySymbolAndTimestampLessThanOrderByTimestampDesc(String symbol, ZonedDateTime timestamp);

    // Get the latest price point for API endpoint
    PricePoint findTopBySymbolOrderByTimestampDesc(String symbol);

    // For history viewing or analytics
    List<PricePoint> findAllBySymbolOrderByTimestampDesc(String symbol);
}
