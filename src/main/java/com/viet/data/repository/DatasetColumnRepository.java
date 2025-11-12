package com.viet.data.repository;

import com.viet.data.entity.DatasetColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetColumnRepository extends JpaRepository<DatasetColumn, Long> {

}
