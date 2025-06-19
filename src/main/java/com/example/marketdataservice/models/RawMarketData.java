package com.example.marketdataservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="T_RAW_MARKET_DATA")
public class RawMarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private ZonedDateTime timestamp;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawResponse;
}
