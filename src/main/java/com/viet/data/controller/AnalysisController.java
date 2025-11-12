
package com.viet.data.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viet.data.dto.AnalysisRequestDTO;
import com.viet.data.dto.AnalysisResultDTO;
import com.viet.data.dto.RecommendationDTO;
import com.viet.data.entity.AnalysisResult;
import com.viet.data.entity.Recommendation;
import com.viet.data.repository.StockDataRepository;
import com.viet.data.services.FinancialAnalysisService;
import com.viet.data.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AnalysisController {

    @Autowired
    private FinancialAnalysisService financialAnalysisService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private StockDataRepository stockDataRepository;

    @PostMapping("/run")
    public ResponseEntity<?> runAnalysis(@RequestBody AnalysisRequestDTO request) {
        try {
            List<AnalysisResult> results = runRequestedAnalyses(request);
            List<AnalysisResultDTO> resultDTOs = convertToDTOs(results);

            return ResponseEntity.ok(Map.of(
                    "analysisResults", resultDTOs,
                    "message", "Analysis completed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/recommendation")
    public ResponseEntity<RecommendationDTO> generateRecommendation(@RequestBody AnalysisRequestDTO request) {
        Recommendation recommendation = recommendationService.generateRecommendation(
                request.getDatasetId(), request.getSymbol()
        );
        RecommendationDTO dto = convertToDTO(recommendation);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/symbols/{datasetId}")
    public ResponseEntity<List<String>> getAvailableSymbols(@PathVariable Long datasetId) {
        List<String> symbols = stockDataRepository.findDistinctSymbolsByDatasetId(datasetId);
        return ResponseEntity.ok(symbols);
    }

    @GetMapping("/results/{datasetId}/{symbol}")
    public ResponseEntity<List<AnalysisResultDTO>> getAnalysisResults(
            @PathVariable Long datasetId,
            @PathVariable String symbol) {
        List<AnalysisResult> results = financialAnalysisService
                .getAnalysisResults(datasetId, symbol);
        List<AnalysisResultDTO> dtos = convertToDTOs(results);
        return ResponseEntity.ok(dtos);
    }

    private List<AnalysisResult> runRequestedAnalyses(AnalysisRequestDTO request) {
        List<AnalysisResult> allResults = new ArrayList<>();

        for (String indicator : request.getIndicators()) {
            switch (indicator.toUpperCase()) {
                case "SMA":
                    allResults.addAll(financialAnalysisService.calculateSMA(
                            request.getDatasetId(), request.getSymbol(),
                            request.getPeriod() != null ? request.getPeriod() : 20
                    ));
                    break;
                case "RSI":
                    allResults.addAll(financialAnalysisService.calculateRSI(
                            request.getDatasetId(), request.getSymbol(),
                            request.getPeriod() != null ? request.getPeriod() : 14
                    ));
                    break;
                case "MACD":
                    allResults.addAll(financialAnalysisService.calculateMACD(
                            request.getDatasetId(), request.getSymbol()
                    ));
                    break;
            }
        }

        return allResults;
    }

    private List<AnalysisResultDTO> convertToDTOs(List<AnalysisResult> results) {
        return results.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private AnalysisResultDTO convertToDTO(AnalysisResult result) {
        AnalysisResultDTO dto = new AnalysisResultDTO();
        dto.setAnalysisType(result.getAnalysisType());
        dto.setDate(result.getResultDate());
        dto.setSignal(result.getSignal());
        dto.setSymbol(result.getSymbol());

        try {
            Object value = new ObjectMapper().readValue(result.getResultValue(), Object.class);
            dto.setValue(value);
        } catch (Exception e) {
            dto.setValue(result.getResultValue());
        }

        return dto;
    }

    private RecommendationDTO convertToDTO(Recommendation recommendation) {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setSymbol(recommendation.getSymbol());
        dto.setRecommendation(recommendation.getFinalRecommendation());
        dto.setConfidenceScore(recommendation.getConfidenceScore());
        dto.setReasoning(recommendation.getReasoning());

        try {
            Object summary = new ObjectMapper().readValue(recommendation.getAnalysisSummary(), Object.class);
            dto.setAnalysisSummary(summary);
        } catch (Exception e) {
            dto.setAnalysisSummary(recommendation.getAnalysisSummary());
        }

        return dto;
    }
}