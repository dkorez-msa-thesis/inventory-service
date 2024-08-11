package dev.dkorez.msathesis.catalog.model;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long productId;
    private Integer quantity;
    private Long orderId;
}
