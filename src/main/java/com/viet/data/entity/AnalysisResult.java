package com.viet.data.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;

@Entity
@Table(name = "analysis_results", indexes = {
        @Index(name = "idx_analysis_dataset_symbol", columnList = "dataset_id, symbol")
})
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "analysis_type", nullable = false)
    private String analysisType;

    @Column(name = "result_date", nullable = false)
    private LocalDate resultDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_value", nullable = false)
    private String resultValue;

    private String signal; // BUY, SELL, HOLD

    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Dataset getDataset() { return dataset; }
    public void setDataset(Dataset dataset) { this.dataset = dataset; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public LocalDate getResultDate() { return resultDate; }
    public void setResultDate(LocalDate resultDate) { this.resultDate = resultDate; }

    public String getResultValue() { return resultValue; }
    public void setResultValue(String resultValue) { this.resultValue = resultValue; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
