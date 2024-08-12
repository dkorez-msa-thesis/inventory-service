package dev.dkorez.msathesis.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRequest {
    private Long productId;
    private Integer quantity;
    private Long orderId;
}
