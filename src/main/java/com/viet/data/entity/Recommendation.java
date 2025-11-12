package com.viet.data.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "final_recommendation", nullable = false)
    private String finalRecommendation; // BUY, SELL, HOLD

    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore;

    private String reasoning;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_summary", nullable = false)
    private String analysisSummary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Dataset getDataset() { return dataset; }
    public void setDataset(Dataset dataset) { this.dataset = dataset; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getFinalRecommendation() { return finalRecommendation; }
    public void setFinalRecommendation(String finalRecommendation) { this.finalRecommendation = finalRecommendation; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public String getAnalysisSummary() { return analysisSummary; }
    public void setAnalysisSummary(String analysisSummary) { this.analysisSummary = analysisSummary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}