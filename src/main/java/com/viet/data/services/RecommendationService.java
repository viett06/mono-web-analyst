package com.viet.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viet.data.entity.AnalysisResult;
import com.viet.data.entity.Dataset;
import com.viet.data.entity.Recommendation;
import com.viet.data.entity.User;
import com.viet.data.exception.AppException;
import com.viet.data.exception.ErrorCode;
import com.viet.data.repository.AnalysisResultRepository;
import com.viet.data.repository.DatasetRepository;
import com.viet.data.repository.RecommendationRepository;
import com.viet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    // Kiểm tra quyền truy cập dataset
    private void validateDatasetAccess( Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new AppException(ErrorCode.DATASET_NOT_FOUND));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!dataset.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Recommendation generateRecommendation( Long datasetId, String symbol) {
        validateDatasetAccess(datasetId);

        List<AnalysisResult> latestResults = analysisResultRepository
                .findLatestAnalysisResults(datasetId, symbol);

        if (latestResults.isEmpty()) {
            throw new AppException(ErrorCode.NO_ANALYSIS_RESULTS);
        }

        Map<String, Integer> signalCounts = new HashMap<>();
        Map<String, Object> analysisSummary = new HashMap<>();

        for (AnalysisResult result : latestResults) {
            String signal = result.getSignal();
            signalCounts.put(signal, signalCounts.getOrDefault(signal, 0) + 1);

            // Add to analysis summary
            try {
                Object value = objectMapper.readValue(result.getResultValue(), Object.class);
                analysisSummary.put(result.getAnalysisType(), Map.of(
                        "value", value,
                        "signal", signal
                ));
            } catch (JsonProcessingException e) {
                // Skip if cannot parse
            }
        }

        String finalRecommendation = determineFinalRecommendation(signalCounts);
        BigDecimal confidenceScore = calculateConfidenceScore(signalCounts, latestResults.size());
        String reasoning = generateReasoning(signalCounts, analysisSummary);

        Recommendation recommendation = new Recommendation();
        Dataset dataset = new Dataset();
        dataset.setId(datasetId);
        recommendation.setDataset(dataset);
        recommendation.setSymbol(symbol);
        recommendation.setFinalRecommendation(finalRecommendation);
        recommendation.setConfidenceScore(confidenceScore);
        recommendation.setReasoning(reasoning);

        try {
            recommendation.setAnalysisSummary(objectMapper.writeValueAsString(analysisSummary));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing analysis summary", e);
        }

        return recommendationRepository.save(recommendation);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Recommendation> getUserRecommendations( Long datasetId) {
        validateDatasetAccess( datasetId);
        return recommendationRepository.findByDatasetId(datasetId);
    }

    // Các phương thức private giữ nguyên...
    private String determineFinalRecommendation(Map<String, Integer> signalCounts) {
        int buyCount = signalCounts.getOrDefault("BUY", 0);
        int sellCount = signalCounts.getOrDefault("SELL", 0);
        int holdCount = signalCounts.getOrDefault("HOLD", 0);

        if (buyCount > sellCount && buyCount > holdCount) {
            return "BUY";
        } else if (sellCount > buyCount && sellCount > holdCount) {
            return "SELL";
        } else {
            return "HOLD";
        }
    }

    private BigDecimal calculateConfidenceScore(Map<String, Integer> signalCounts, int totalSignals) {
        if (totalSignals == 0) return BigDecimal.ZERO;

        int maxCount = signalCounts.values().stream().max(Integer::compareTo).orElse(0);
        return BigDecimal.valueOf(maxCount)
                .divide(BigDecimal.valueOf(totalSignals), 4, RoundingMode.HALF_UP);
    }

    private String generateReasoning(Map<String, Integer> signalCounts, Map<String, Object> analysisSummary) {
        StringBuilder reasoning = new StringBuilder();

        int buyCount = signalCounts.getOrDefault("BUY", 0);
        int sellCount = signalCounts.getOrDefault("SELL", 0);
        int holdCount = signalCounts.getOrDefault("HOLD", 0);

        reasoning.append(String.format("Analysis based on %d indicators: ", buyCount + sellCount + holdCount));
        reasoning.append(String.format("%d BUY signals, %d SELL signals, %d HOLD signals. ", buyCount, sellCount, holdCount));

        if (analysisSummary.containsKey("RSI_14")) {
            Map<?, ?> rsiData = (Map<?, ?>) analysisSummary.get("RSI_14");
            String rsiSignal = (String) rsiData.get("signal");
            reasoning.append(String.format("RSI indicates %s. ", rsiSignal));
        }

        if (analysisSummary.containsKey("MACD")) {
            Map<?, ?> macdData = (Map<?, ?>) analysisSummary.get("MACD");
            String macdSignal = (String) macdData.get("signal");
            reasoning.append(String.format("MACD shows %s trend. ", macdSignal));
        }

        return reasoning.toString();
    }
}