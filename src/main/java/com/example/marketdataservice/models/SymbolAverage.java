package com.example.marketdataservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_SYMBOL_AVERAGE")
public class SymbolAverage {

    @Id
    @GeneratedValue
    private UUID id;

    private String symbol;

    private double movingAverage;

    private ZonedDateTime timestamp;
}
