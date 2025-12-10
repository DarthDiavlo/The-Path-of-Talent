package com.example.nftfabric.repository;

import com.example.nftfabric.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ImageDataRepository extends JpaRepository<ImageData, Long> {

    // ищем по категории и уровню
    @Query("SELECT i FROM ImageData i WHERE i.subcategory = :subcategory AND i.level = :level")
    List<ImageData> findBySubcategoryAndLevel(String subcategory, Integer level);
}
