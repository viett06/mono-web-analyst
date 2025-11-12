package com.viet.data.dtos;

import java.time.LocalDate;

public class AnalysisResultDTO {
    private String analysisType;
    private LocalDate date;
    private Object value;
    private String signal;
    private String symbol;

    public AnalysisResultDTO() {}

    public AnalysisResultDTO(String analysisType, LocalDate date, Object value, String signal) {
        this.analysisType = analysisType;
        this.date = date;
        this.value = value;
        this.signal = signal;
    }

    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}