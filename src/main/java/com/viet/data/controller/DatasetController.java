package com.viet.data.controller;

import com.viet.data.dto.DatasetUploadDTO;
import com.viet.data.entity.Dataset;
import com.viet.data.entity.User;
import com.viet.data.services.DatasetService;
import com.viet.data.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/datasets")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDataset(@ModelAttribute DatasetUploadDTO uploadDTO) {
        try {
            // TODO: Get current user from authentication
            // For now, using a demo user
            User user = userService.findByUsername("demo").orElseGet(() ->
                    userService.createUser("demo", "demo@example.com", "password")
            );

            Dataset dataset = datasetService.uploadDataset(user, uploadDTO.getName(), uploadDTO.getFile());
            return ResponseEntity.ok(dataset);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Dataset>> getUserDatasets() {
        // TODO: Get current user ID from authentication
        Long userId = 1L; // Demo user ID
        List<Dataset> datasets = datasetService.getUserDatasets(userId);
        return ResponseEntity.ok(datasets);
    }

    @GetMapping("/{datasetId}")
    public ResponseEntity<Dataset> getDataset(@PathVariable Long datasetId) {
        Optional<Dataset> dataset = datasetService.getDataset(datasetId);
        return dataset.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{datasetId}")
    public ResponseEntity<?> deleteDataset(@PathVariable Long datasetId) {
        datasetService.deleteDataset(datasetId);
        return ResponseEntity.ok().build();
    }
}