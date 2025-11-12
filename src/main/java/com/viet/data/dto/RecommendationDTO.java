package com.viet.data.dto;

import java.math.BigDecimal;

public class RecommendationDTO {
    private String symbol;
    private String recommendation;
    private BigDecimal confidenceScore;
    private String reasoning;
    private Object analysisSummary;


    public RecommendationDTO() {}

    public RecommendationDTO(String symbol, String recommendation, BigDecimal confidenceScore) {
        this.symbol = symbol;
        this.recommendation = recommendation;
        this.confidenceScore = confidenceScore;
    }


    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public Object getAnalysisSummary() { return analysisSummary; }
    public void setAnalysisSummary(Object analysisSummary) { this.analysisSummary = analysisSummary; }
}