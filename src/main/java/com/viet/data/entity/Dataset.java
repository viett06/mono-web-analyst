package com.viet.data.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "datasets")
public class Dataset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "column_count")
    private Integer columnCount;

    private String status = "UPLOADED";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DatasetColumn> columns = new HashSet<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StockData> stockData = new HashSet<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AnalysisResult> analysisResults = new HashSet<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Recommendation> recommendations = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Dataset() {}

    public Dataset(User user, String name, String originalFilename) {
        this.user = user;
        this.name = name;
        this.originalFilename = originalFilename;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getRowCount() { return rowCount; }
    public void setRowCount(Integer rowCount) { this.rowCount = rowCount; }

    public Integer getColumnCount() { return columnCount; }
    public void setColumnCount(Integer columnCount) { this.columnCount = columnCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<DatasetColumn> getColumns() { return columns; }
    public void setColumns(Set<DatasetColumn> columns) { this.columns = columns; }

    public Set<StockData> getStockData() { return stockData; }
    public void setStockData(Set<StockData> stockData) { this.stockData = stockData; }

    public Set<AnalysisResult> getAnalysisResults() { return analysisResults; }
    public void setAnalysisResults(Set<AnalysisResult> analysisResults) { this.analysisResults = analysisResults; }

    public Set<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(Set<Recommendation> recommendations) { this.recommendations = recommendations; }
}