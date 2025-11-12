package com.viet.data.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.viet.data.entity.Dataset;
import com.viet.data.entity.DatasetColumn;
import com.viet.data.entity.StockData;
import com.viet.data.entity.User;
import com.viet.data.repository.DatasetColumnRepository;
import com.viet.data.repository.DatasetRepository;
import com.viet.data.repository.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FileProcessingService {

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private StockDataRepository stockDataRepository;

    @Autowired
    private DatasetColumnRepository datasetColumnRepository;

    public Dataset processCSVFile(MultipartFile file, User user, String datasetName) throws IOException {
        // Create dataset entity
        Dataset dataset = new Dataset(user, datasetName, file.getOriginalFilename());
        dataset.setFileSize(file.getSize());
        dataset.setStatus("PROCESSING");

        Dataset savedDataset = datasetRepository.save(dataset);

        try {
            Set<StockData> stockDataSet = parseCSVFile(file, savedDataset);
            dataset.setStockData(stockDataSet);
            dataset.setRowCount(stockDataSet.size());
            dataset.setStatus("COMPLETED");

            return datasetRepository.save(dataset);
        } catch (Exception e) {
            dataset.setStatus("ERROR");
            datasetRepository.save(dataset);
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    private Set<StockData> parseCSVFile(MultipartFile file, Dataset dataset) throws IOException {
        Set<StockData> stockDataSet = new HashSet<>();
        Set<DatasetColumn> columns = new HashSet<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                throw new RuntimeException("CSV file is empty");
            }


            // Create column metadata
            for (int i = 0; i < headers.length; i++) {
                
                DatasetColumn column = new DatasetColumn();
                column.setDataset(dataset);
                column.setColumnName(headers[i].trim());
                column.setColumnIndex(i);
                column.setDataType("STRING"); // Default, will be updated later
                columns.add(column);
            }
            dataset.setColumns(columns);

            dataset.setColumnCount(headers.length);

            String[] line;
            int rowCount = 0;
            while ((line = reader.readNext()) != null) {
                if (line.length != headers.length) {
                    continue; // Skip invalid rows
                }

                StockData stockData = createStockDataFromRow(line, headers, dataset);
                if (stockData != null) {
                    stockDataSet.add(stockData);
                    rowCount++;
                }
            }

            dataset.setRowCount(rowCount);
        } catch (CsvValidationException e) {
            throw new RuntimeException("CSV validation error: " + e.getMessage(), e);
        }

        stockDataRepository.saveAll(stockDataSet);
        return stockDataSet;
    }

    private StockData createStockDataFromRow(String[] row, String[] headers, Dataset dataset) {
        try {
            StockData stockData = new StockData();
            stockData.setDataset(dataset);

            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].toLowerCase();
                String value = row[i].trim();

                if (value.isEmpty()) continue;

                switch (header) {
                    case "symbol", "ticker" -> stockData.setSymbol(value);
                    case "date" -> stockData.setDate(parseDate(value));
                    case "open" -> stockData.setOpenPrice(new BigDecimal(value));
                    case "high" -> stockData.setHighPrice(new BigDecimal(value));
                    case "low" -> stockData.setLowPrice(new BigDecimal(value));
                    case "close" -> stockData.setClosePrice(new BigDecimal(value));
                    case "volume" -> stockData.setVolume(Long.parseLong(value));
                    case "adjusted close", "adj_close" -> stockData.setAdjustedClose(new BigDecimal(value));
                }
            }

            // Validate required fields
            if (stockData.getSymbol() == null || stockData.getDate() == null || stockData.getClosePrice() == null) {
                return null;
            }

            return stockData;
        } catch (Exception e) {
            // Skip rows with parsing errors
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            // Try different date formats
            DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                    DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
            };

            for (DateTimeFormatter formatter : formatters) {
                try {
                    return LocalDate.parse(dateStr, formatter);
                } catch (Exception e) {
                    // Try next format
                }
            }
            throw new IllegalArgumentException("Unsupported date format: " + dateStr);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing date: " + dateStr, e);
        }
    }
}