package com.viet.data.repository;

import com.viet.data.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByDatasetIdAndSymbolAndAnalysisTypeOrderByResultDateAsc(
            Long datasetId, String symbol, String analysisType);
    List<AnalysisResult> findByDatasetIdAndSymbolOrderByResultDateAsc(Long datasetId, String symbol);


    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.dataset.id = :datasetId AND ar.symbol = :symbol ORDER BY ar.resultDate DESC")
    List<AnalysisResult> findLatestAnalysisResults(
            @Param("datasetId") Long datasetId,
            @Param("symbol") String symbol);
}