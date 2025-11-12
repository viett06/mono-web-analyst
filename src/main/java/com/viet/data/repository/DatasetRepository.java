package com.viet.data.repository;

import com.viet.data.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByUserId(Long userId);

    @Query("SELECT d FROM Dataset d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
    List<Dataset> findRecentDatasetsByUserId(@Param("userId") Long userId);
}