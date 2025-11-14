package com.viet.data.services;

import com.viet.data.entity.Dataset;
import com.viet.data.entity.User;
import com.viet.data.exception.AppException;
import com.viet.data.exception.ErrorCode;
import com.viet.data.repository.DatasetRepository;
import com.viet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Dataset uploadDataset( String name, org.springframework.web.multipart.MultipartFile file) throws IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return fileProcessingService.processCSVFile(file, user, name);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")

    public List<Dataset> getUserDatasets() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return datasetRepository.findByUserId(user.getId());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Optional<Dataset> getDataset( Long datasetId) {
        Optional<Dataset> dataset = datasetRepository.findById(datasetId);
        // Kiểm tra xem dataset có thuộc về user không
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        if (dataset.isPresent() && !dataset.get().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        return dataset;
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")

    public void deleteDataset(Long datasetId) {
        Optional<Dataset> dataset = datasetRepository.findById(datasetId);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        if (dataset.isPresent() && !dataset.get().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        datasetRepository.deleteById(datasetId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Dataset> getMyDatasets() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return datasetRepository.findRecentDatasetsByUserId(user.getId());
    }
}