package com.warehouse.repository;

import com.warehouse.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findBySku(String sku);
    List<Item> findByCategoryIgnoreCase(String category);
    List<Item> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku);
    boolean existsBySku(String sku);
}
