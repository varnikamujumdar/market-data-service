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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_POLLING_CONFIG")
public class PollingJobConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String jobId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String symbolsJson; // comma-separated or JSON array of symbols

    @Column(nullable = false)
    private Integer interval; // in seconds or minutes

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String status; // accepted, running, failed, completed

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    private ZonedDateTime lastExecutedAt;
}
