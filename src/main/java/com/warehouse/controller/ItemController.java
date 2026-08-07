package com.warehouse.controller;

import com.warehouse.dto.ItemDtos.ItemRequest;
import com.warehouse.dto.ItemDtos.ItemResponse;
import com.warehouse.dto.ItemDtos.StockAdjustRequest;
import com.warehouse.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemResponse> list(@RequestParam(required = false) String query,
                                    @RequestParam(required = false) String category) {
        return itemService.search(query, category).stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        return ItemResponse.from(itemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest request) {
        return ResponseEntity.ok(ItemResponse.from(itemService.create(request)));
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return ItemResponse.from(itemService.update(id, request));
    }

    @PatchMapping("/{id}/stock")
    public ItemResponse adjustStock(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest request) {
        return ItemResponse.from(itemService.adjustStock(id, request.delta()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
