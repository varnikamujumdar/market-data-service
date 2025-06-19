package com.example.marketdataservice.repository;

import com.example.marketdataservice.models.PollingJobConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface PollingJobConfigRepository extends JpaRepository<PollingJobConfig, UUID> {
    Optional<PollingJobConfigRepository> findByJobId(String jobId);

    List<PollingJobConfig> findByStatus(String status);
}
