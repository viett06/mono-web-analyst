package com.viet.data.controller;

import com.viet.data.dtos.DatasetUploadDTO;
import com.viet.data.dtos.response.ApiResponse;
import com.viet.data.entity.Dataset;
import com.viet.data.services.DatasetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/datasets")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    @PostMapping("/upload")
    public ApiResponse<Dataset> uploadDataset(@ModelAttribute DatasetUploadDTO uploadDTO) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} uploading dataset: {}", authentication.getName(), uploadDTO.getName());

            Dataset dataset = datasetService.uploadDataset(uploadDTO.getName(), uploadDTO.getFile());

            return ApiResponse.<Dataset>builder()
                    .result(dataset)
                    .message("Dataset uploaded successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error uploading dataset: {}", e.getMessage());
            return ApiResponse.<Dataset>builder()
                    .code(400)
                    .message("Error uploading dataset: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<List<Dataset>> getUserDatasets() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} fetching datasets", authentication.getName());

            List<Dataset> datasets = datasetService.getUserDatasets();

            return ApiResponse.<List<Dataset>>builder()
                    .result(datasets)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching user datasets: {}", e.getMessage());
            return ApiResponse.<List<Dataset>>builder()
                    .code(400)
                    .message("Error fetching datasets: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/my-datasets")
    public ApiResponse<List<Dataset>> getMyDatasets() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} fetching their datasets", authentication.getName());

            List<Dataset> datasets = datasetService.getMyDatasets();

            return ApiResponse.<List<Dataset>>builder()
                    .result(datasets)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching user datasets: {}", e.getMessage());
            return ApiResponse.<List<Dataset>>builder()
                    .code(400)
                    .message("Error fetching datasets: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{datasetId}")
    public ApiResponse<Dataset> getDataset(@PathVariable Long datasetId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} fetching dataset {}", authentication.getName(), datasetId);

            Optional<Dataset> dataset = datasetService.getDataset(datasetId);

            if (dataset.isPresent()) {
                return ApiResponse.<Dataset>builder()
                        .result(dataset.get())
                        .build();
            } else {
                return ApiResponse.<Dataset>builder()
                        .code(404)
                        .message("Dataset not found")
                        .build();
            }

        } catch (Exception e) {
            log.error("Error fetching dataset {}: {}", datasetId, e.getMessage());
            return ApiResponse.<Dataset>builder()
                    .code(400)
                    .message("Error fetching dataset: " + e.getMessage())
                    .build();
        }
    }

    @DeleteMapping("/{datasetId}")
    public ApiResponse<String> deleteDataset(@PathVariable Long datasetId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} deleting dataset {}", authentication.getName(), datasetId);

            datasetService.deleteDataset(datasetId);

            return ApiResponse.<String>builder()
                    .result("Dataset deleted successfully")
                    .message("Dataset deleted successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error deleting dataset {}: {}", datasetId, e.getMessage());
            return ApiResponse.<String>builder()
                    .code(400)
                    .message("Error deleting dataset: " + e.getMessage())
                    .build();
        }
    }
}