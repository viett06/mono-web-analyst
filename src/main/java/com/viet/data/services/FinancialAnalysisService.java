package com.viet.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viet.data.entity.AnalysisResult;
import com.viet.data.entity.Dataset;
import com.viet.data.entity.StockData;
import com.viet.data.entity.User;
import com.viet.data.exception.AppException;
import com.viet.data.exception.ErrorCode;
import com.viet.data.repository.AnalysisResultRepository;
import com.viet.data.repository.DatasetRepository;
import com.viet.data.repository.StockDataRepository;
import com.viet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialAnalysisService {

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

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
    public List<AnalysisResult> getAnalysisResults( Long datasetId, String symbol) {
        validateDatasetAccess( datasetId);
        return analysisResultRepository.findByDatasetIdAndSymbolOrderByResultDateAsc(datasetId, symbol);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<AnalysisResult> calculateSMA( Long datasetId, String symbol, int period) {
        validateDatasetAccess( datasetId);
        List<StockData> stockDataList = stockDataRepository.findByDatasetIdAndSymbolOrderByDateAsc(datasetId, symbol);

        if (stockDataList.isEmpty()) {
            throw new AppException(ErrorCode.NO_DATA_FOUND);
        }

        List<AnalysisResult> results = new ArrayList<>();

        for (int i = period - 1; i < stockDataList.size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            // cua so truot
            for (int j = i - period + 1; j <= i; j++) {
                sum = sum.add(stockDataList.get(j).getClosePrice());
            }
            BigDecimal sma = sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);

            AnalysisResult result = createAnalysisResult(
                    datasetId, symbol, "SMA_" + period,
                    stockDataList.get(i).getDate(), sma, determineSMASignal(stockDataList, i, sma)
            );
            results.add(result);
        }

        return analysisResultRepository.saveAll(results);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<AnalysisResult> calculateRSI( Long datasetId, String symbol, int period) {
        validateDatasetAccess( datasetId);
        List<StockData> stockDataList = stockDataRepository.findByDatasetIdAndSymbolOrderByDateAsc(datasetId, symbol);
        List<AnalysisResult> results = new ArrayList<>();

        if (stockDataList.size() <= period) {
            throw new AppException(ErrorCode.INSUFFICIENT_DATA);
        }

        List<BigDecimal> gains = new ArrayList<>();
        List<BigDecimal> losses = new ArrayList<>();

        // Calculate price changes
        for (int i = 1; i < stockDataList.size(); i++) {
            BigDecimal change = stockDataList.get(i).getClosePrice()
                    .subtract(stockDataList.get(i - 1).getClosePrice());
            gains.add(change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO);
            losses.add(change.compareTo(BigDecimal.ZERO) < 0 ? change.abs() : BigDecimal.ZERO);
        }

        // Calculate RSI
        for (int i = period; i < gains.size(); i++) {
            BigDecimal avgGain = calculateAverage(gains.subList(i - period, i));
            BigDecimal avgLoss = calculateAverage(losses.subList(i - period, i));

            if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal rsi = BigDecimal.valueOf(100);
                AnalysisResult result = createAnalysisResult(
                        datasetId, symbol, "RSI_" + period,
                        stockDataList.get(i + 1).getDate(), rsi, determineRSISignal(rsi)
                );
                results.add(result);
            } else {
                BigDecimal rs = avgGain.divide(avgLoss, 4, RoundingMode.HALF_UP);
                BigDecimal rsi = BigDecimal.valueOf(100).subtract(
                        BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 4, RoundingMode.HALF_UP)
                );
                AnalysisResult result = createAnalysisResult(
                        datasetId, symbol, "RSI_" + period,
                        stockDataList.get(i + 1).getDate(), rsi, determineRSISignal(rsi)
                );
                results.add(result);
            }
        }

        return analysisResultRepository.saveAll(results);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<AnalysisResult> calculateMACD( Long datasetId, String symbol) {
        validateDatasetAccess(datasetId);
        List<StockData> stockDataList = stockDataRepository.findByDatasetIdAndSymbolOrderByDateAsc(datasetId, symbol);

        if (stockDataList.size() < 35) { // Cần ít nhất 35 ngày cho MACD
            throw new AppException(ErrorCode.INSUFFICIENT_DATA);
        }

        List<AnalysisResult> results = new ArrayList<>();

        // Calculate EMAs
        List<BigDecimal> ema12 = calculateEMA(stockDataList, 12);
        List<BigDecimal> ema26 = calculateEMA(stockDataList, 26);

        // Calculate MACD line
        List<BigDecimal> macdLine = new ArrayList<>();
        for (int i = 0; i < Math.min(ema12.size(), ema26.size()); i++) {
            macdLine.add(ema12.get(i).subtract(ema26.get(i)));
        }

        // Calculate Signal line (EMA of MACD line)
        List<BigDecimal> signalLine = calculateEMAForList(macdLine, 9);

        // Calculate Histogram
        for (int i = 0; i < Math.min(macdLine.size(), signalLine.size()); i++) {
            Map<String, BigDecimal> macdValues = new HashMap<>();
            macdValues.put("macd", macdLine.get(i));
            macdValues.put("signal", signalLine.get(i));
            macdValues.put("histogram", macdLine.get(i).subtract(signalLine.get(i)));

            AnalysisResult result = createAnalysisResult(
                    datasetId, symbol, "MACD",
                    stockDataList.get(i + 25).getDate(), // Offset for EMA calculations
                    macdValues, determineMACDSignal(macdLine.get(i), signalLine.get(i))
            );
            results.add(result);
        }

        return analysisResultRepository.saveAll(results);
    }


    private List<BigDecimal> calculateEMA(List<StockData> stockData, int period) {
        List<BigDecimal> emaValues = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));

        // First EMA is SMA
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(stockData.get(i).getClosePrice());
        }
        BigDecimal ema = sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
        emaValues.add(ema);

        // Subsequent EMAs
        for (int i = period; i < stockData.size(); i++) {
            ema = stockData.get(i).getClosePrice()
                    .multiply(multiplier)
                    .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
            emaValues.add(ema);
        }

        return emaValues;
    }

    private List<BigDecimal> calculateEMAForList(List<BigDecimal> values, int period) {
        List<BigDecimal> emaValues = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));

        // First EMA is SMA
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(values.get(i));
        }
        BigDecimal ema = sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
        emaValues.add(ema);

        // Subsequent EMAs
        for (int i = period; i < values.size(); i++) {
            ema = values.get(i).multiply(multiplier)
                    .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
            emaValues.add(ema);
        }

        return emaValues;
    }

    private BigDecimal calculateAverage(List<BigDecimal> values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            sum = sum.add(value);
        }
        return sum.divide(BigDecimal.valueOf(values.size()), 4, RoundingMode.HALF_UP);
    }

    private AnalysisResult createAnalysisResult(Long datasetId, String symbol, String analysisType,
                                                LocalDate date, Object value, String signal) {
        AnalysisResult result = new AnalysisResult();
        Dataset dataset = new Dataset();
        dataset.setId(datasetId);
        result.setDataset(dataset);
        result.setSymbol(symbol);
        result.setAnalysisType(analysisType);
        result.setResultDate(date);
        result.setSignal(signal);

        try {
            result.setResultValue(objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing analysis result", e);
        }

        return result;
    }

    private String determineSMASignal(List<StockData> stockData, int index, BigDecimal sma) {
        BigDecimal currentPrice = stockData.get(index).getClosePrice();
        int compare = currentPrice.compareTo(sma);
        return compare > 0 ? "BUY" : compare < 0 ? "SELL" : "HOLD";
    }

    private String determineRSISignal(BigDecimal rsi) {
        if (rsi.compareTo(BigDecimal.valueOf(70)) > 0) {
            return "SELL"; // Overbought
        } else if (rsi.compareTo(BigDecimal.valueOf(30)) < 0) {
            return "BUY"; // Oversold
        } else {
            return "HOLD";
        }
    }

    private String determineMACDSignal(BigDecimal macd, BigDecimal signal) {
        int compare = macd.compareTo(signal);
        return compare > 0 ? "BUY" : compare < 0 ? "SELL" : "HOLD";
    }
}