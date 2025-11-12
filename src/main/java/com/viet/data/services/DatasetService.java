package com.viet.data.services;

import com.viet.data.entity.Dataset;
import com.viet.data.entity.User;
import com.viet.data.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private FileProcessingService fileProcessingService;

    public Dataset uploadDataset(User user, String name, org.springframework.web.multipart.MultipartFile file) throws IOException {
        return fileProcessingService.processCSVFile(file, user, name);
    }

    public List<Dataset> getUserDatasets(Long userId) {
        return datasetRepository.findByUserId(userId);
    }

    public Optional<Dataset> getDataset(Long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    public void deleteDataset(Long datasetId) {
        datasetRepository.deleteById(datasetId);
    }
}