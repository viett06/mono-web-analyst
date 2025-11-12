package com.viet.data.repository;

import com.viet.data.entity.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
    List<StockData> findByDatasetIdAndSymbolOrderByDateAsc(Long datasetId, String symbol);

    @Query("SELECT sd FROM StockData sd WHERE sd.dataset.id = :datasetId AND sd.symbol = :symbol AND sd.date BETWEEN :startDate AND :endDate ORDER BY sd.date ASC")
    List<StockData> findByDatasetAndSymbolAndDateRange(
            @Param("datasetId") Long datasetId,
            @Param("symbol") String symbol,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT sd.symbol FROM StockData sd WHERE sd.dataset.id = :datasetId")
    List<String> findDistinctSymbolsByDatasetId(@Param("datasetId") Long datasetId);
}