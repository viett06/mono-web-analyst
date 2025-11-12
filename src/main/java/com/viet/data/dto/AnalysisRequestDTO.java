package com.viet.data.dto;
import java.util.List;


public class AnalysisRequestDTO {
    private Long datasetId;
    private String symbol;
    private List<String> indicators;
    private Integer period;


    public AnalysisRequestDTO() {}

    public AnalysisRequestDTO(Long datasetId, String symbol, List<String> indicators) {
        this.datasetId = datasetId;
        this.symbol = symbol;
        this.indicators = indicators;
    }


    public Long getDatasetId() { return datasetId; }
    public void setDatasetId(Long datasetId) { this.datasetId = datasetId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public List<String> getIndicators() { return indicators; }
    public void setIndicators(List<String> indicators) { this.indicators = indicators; }

    public Integer getPeriod() { return period; }
    public void setPeriod(Integer period) { this.period = period; }
}
