package com.example.marketdataservice.repository;

import com.example.marketdataservice.models.SymbolAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SymbolAverageRepository extends JpaRepository<SymbolAverage, UUID> {


}
