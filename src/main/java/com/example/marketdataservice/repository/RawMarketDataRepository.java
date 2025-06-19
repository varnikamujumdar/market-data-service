package com.example.marketdataservice.repository;

import com.example.marketdataservice.models.RawMarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
    public interface RawMarketDataRepository extends JpaRepository<RawMarketData, UUID> {
}
