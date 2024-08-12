package dev.dkorez.msathesis.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryRequest {
    private Long productId;
    private Integer quantity;
}
