package com.warehouse.service;

import com.warehouse.dto.ItemDtos.ItemRequest;
import com.warehouse.exception.ConflictException;
import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.model.Item;
import com.warehouse.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final Random random = new Random();

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public List<Item> search(String query, String category) {
        List<Item> base = (query == null || query.isBlank())
                ? itemRepository.findAll()
                : itemRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(query, query);

        if (category == null || category.isBlank() || category.equalsIgnoreCase("all")) {
            return base;
        }
        return base.stream().filter(i -> i.getCategory().equalsIgnoreCase(category)).toList();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
    }

    public Item create(ItemRequest req) {
        String sku = generateSku();
        Item item = Item.builder()
                .sku(sku)
                .name(req.name())
                .category(req.category())
                .stock(req.stock())
                .reorderPoint(req.reorderPoint())
                .unitCost(req.unitCost())
                .zone(req.zone() != null && !req.zone().isBlank() ? req.zone() : randomZone())
                .aisle(req.aisle() != null ? req.aisle() : 1 + random.nextInt(4))
                .bin(req.bin() != null ? req.bin() : 1 + random.nextInt(6))
                .build();
        return itemRepository.save(item);
    }

    public Item update(Long id, ItemRequest req) {
        Item item = findById(id);
        item.setName(req.name());
        item.setCategory(req.category());
        item.setStock(req.stock());
        item.setReorderPoint(req.reorderPoint());
        item.setUnitCost(req.unitCost());
        if (req.zone() != null && !req.zone().isBlank()) item.setZone(req.zone());
        if (req.aisle() != null) item.setAisle(req.aisle());
        if (req.bin() != null) item.setBin(req.bin());
        return itemRepository.save(item);
    }

    public Item adjustStock(Long id, int delta) {
        Item item = findById(id);
        int newStock = item.getStock() + delta;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot go below zero (current: " + item.getStock() + ", requested change: " + delta + ")");
        }
        item.setStock(newStock);
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found: " + id);
        }
        itemRepository.deleteById(id);
    }

    private String randomZone() {
        String[] zones = {"A", "B", "C", "D"};
        return zones[random.nextInt(zones.length)];
    }

    private String generateSku() {
        String candidate;
        do {
            candidate = "SKU-" + (1000 + random.nextInt(9000));
        } while (itemRepository.existsBySku(candidate));
        return candidate;
    }
}
