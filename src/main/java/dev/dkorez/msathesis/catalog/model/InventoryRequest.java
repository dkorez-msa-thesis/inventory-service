package dev.dkorez.msathesis.catalog.model;

import lombok.Data;

@Data
public class InventoryRequest {
    private Long productId;
    private Integer quantity;
}
