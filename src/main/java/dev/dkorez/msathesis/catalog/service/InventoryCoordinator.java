package dev.dkorez.msathesis.catalog.service;

import dev.dkorez.msathesis.catalog.messaging.InventoryEvent;
import dev.dkorez.msathesis.catalog.messaging.InventoryEventType;
import dev.dkorez.msathesis.catalog.model.InventoryRequest;
import dev.dkorez.msathesis.catalog.model.ReservationRequest;
import dev.dkorez.msathesis.catalog.messaging.InventoryEventProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InventoryCoordinator {
    private final InventoryService inventoryService;
    private final InventoryEventProducer eventProducer;

    @Inject
    public InventoryCoordinator(InventoryService inventoryService, InventoryEventProducer eventProducer) {
        this.inventoryService = inventoryService;
        this.eventProducer = eventProducer;
    }

    public void reserveQuantity(ReservationRequest request, boolean sendEvent) {
        inventoryService.reserveQuantity(request);

        if (sendEvent) {
            InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_RESERVED, request.getProductId(), request.getQuantity(), request.getOrderId());
            eventProducer.sendEvent(event);
        }
    }

    public void cancelReservation(ReservationRequest request, boolean sendEvent) {
        inventoryService.cancelReservation(request);

        if (sendEvent) {
            InventoryEvent event = new InventoryEvent(InventoryEventType.RESERVATION_RELEASED, request.getProductId(), request.getQuantity(), request.getOrderId());
            eventProducer.sendEvent(event);
        }
    }

    public void updateQuantity(InventoryRequest request, boolean sendEvent) {
        inventoryService.updateQuantity(request);

        if (sendEvent) {
            InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_UPDATED, request.getProductId(), request.getQuantity(), null);
            eventProducer.sendEvent(event);
        }
    }

    public void deleteInventory(Long productId) {
        inventoryService.deleteInventory(productId);
    }
}
