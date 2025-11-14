package com.viet.data.controller;

import com.viet.data.dtos.response.ApiResponse;
import com.viet.data.entity.AnalysisResult;
import com.viet.data.services.FinancialAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AnalysisController {

    @Autowired
    private FinancialAnalysisService financialAnalysisService;

    @GetMapping("/{datasetId}/results")
    public ApiResponse<List<AnalysisResult>> getAnalysisResults(
            @PathVariable Long datasetId,
            @RequestParam String symbol) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} fetching analysis results for dataset {} symbol {}",
                    authentication.getName(), datasetId, symbol);

            List<AnalysisResult> results = financialAnalysisService.getAnalysisResults(datasetId, symbol);

            return ApiResponse.<List<AnalysisResult>>builder()
                    .result(results)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching analysis results for dataset {} symbol {}: {}",
                    datasetId, symbol, e.getMessage());
            return ApiResponse.<List<AnalysisResult>>builder()
                    .code(400)
                    .message("Error fetching analysis results: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/{datasetId}/sma")
    public ApiResponse<List<AnalysisResult>> calculateSMA(
            @PathVariable Long datasetId,
            @RequestParam String symbol,
            @RequestParam(defaultValue = "14") int period) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} calculating SMA for dataset {} symbol {} period {}",
                    authentication.getName(), datasetId, symbol, period);

            List<AnalysisResult> results = financialAnalysisService.calculateSMA(datasetId, symbol, period);

            return ApiResponse.<List<AnalysisResult>>builder()
                    .result(results)
                    .message("SMA calculation completed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error calculating SMA for dataset {} symbol {} period {}: {}",
                    datasetId, symbol, period, e.getMessage());
            return ApiResponse.<List<AnalysisResult>>builder()
                    .code(400)
                    .message("Error calculating SMA: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/{datasetId}/rsi")
    public ApiResponse<List<AnalysisResult>> calculateRSI(
            @PathVariable Long datasetId,
            @RequestParam String symbol,
            @RequestParam(defaultValue = "14") int period) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} calculating RSI for dataset {} symbol {} period {}",
                    authentication.getName(), datasetId, symbol, period);

            List<AnalysisResult> results = financialAnalysisService.calculateRSI(datasetId, symbol, period);

            return ApiResponse.<List<AnalysisResult>>builder()
                    .result(results)
                    .message("RSI calculation completed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error calculating RSI for dataset {} symbol {} period {}: {}",
                    datasetId, symbol, period, e.getMessage());
            return ApiResponse.<List<AnalysisResult>>builder()
                    .code(400)
                    .message("Error calculating RSI: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/{datasetId}/macd")
    public ApiResponse<List<AnalysisResult>> calculateMACD(
            @PathVariable Long datasetId,
            @RequestParam String symbol) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} calculating MACD for dataset {} symbol {}",
                    authentication.getName(), datasetId, symbol);

            List<AnalysisResult> results = financialAnalysisService.calculateMACD(datasetId, symbol);

            return ApiResponse.<List<AnalysisResult>>builder()
                    .result(results)
                    .message("MACD calculation completed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error calculating MACD for dataset {} symbol {}: {}",
                    datasetId, symbol, e.getMessage());
            return ApiResponse.<List<AnalysisResult>>builder()
                    .code(400)
                    .message("Error calculating MACD: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/{datasetId}/all")
    public ApiResponse<List<AnalysisResult>> calculateAllIndicators(
            @PathVariable Long datasetId,
            @RequestParam String symbol,
            @RequestParam(defaultValue = "14") int period) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} calculating all indicators for dataset {} symbol {} period {}",
                    authentication.getName(), datasetId, symbol, period);

            // Tính tất cả các chỉ báo
            List<AnalysisResult> smaResults = financialAnalysisService.calculateSMA(datasetId, symbol, period);
            List<AnalysisResult> rsiResults = financialAnalysisService.calculateRSI(datasetId, symbol, period);
            List<AnalysisResult> macdResults = financialAnalysisService.calculateMACD(datasetId, symbol);

            // Kết hợp tất cả kết quả
            List<AnalysisResult> allResults = new ArrayList<>();
            allResults.addAll(smaResults);
            allResults.addAll(rsiResults);
            allResults.addAll(macdResults);

            return ApiResponse.<List<AnalysisResult>>builder()
                    .result(allResults)
                    .message("All indicators calculated successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error calculating all indicators for dataset {} symbol {}: {}",
                    datasetId, symbol, e.getMessage());
            return ApiResponse.<List<AnalysisResult>>builder()
                    .code(400)
                    .message("Error calculating indicators: " + e.getMessage())
                    .build();
        }
    }
}