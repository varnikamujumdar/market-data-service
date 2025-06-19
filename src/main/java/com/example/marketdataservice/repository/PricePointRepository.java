package com.example.marketdataservice.repository;


import com.example.marketdataservice.models.PricePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PricePointRepository extends JpaRepository<PricePoint, UUID> {

    List<PricePoint> findBySymbolAndProviderAndTimestampBetweenOrderByTimestampAsc(
            String symbol, String provider, ZonedDateTime start, ZonedDateTime end);
}
