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

    public void reserveQuantity(ReservationRequest request) {
        inventoryService.reserveQuantity(request);

        InventoryEvent event = new InventoryEvent();
        event.setType(InventoryEventType.INVENTORY_RESERVED);
        event.setProductId(request.getProductId());
        event.setQuantity(request.getQuantity());
        event.setOrderId(request.getOrderId());
        eventProducer.sendEvent(event);
    }

    public void cancelReservation(ReservationRequest request) {
        inventoryService.cancelReservation(request);

        InventoryEvent event = new InventoryEvent();
        event.setType(InventoryEventType.INVENTORY_RELEASED);
        event.setProductId(request.getProductId());
        event.setQuantity(request.getQuantity());
        event.setOrderId(request.getOrderId());
        eventProducer.sendEvent(event);
    }

    public void updateQuantity(InventoryRequest request) {
        inventoryService.updateQuantity(request);

        InventoryEvent event = new InventoryEvent();
        event.setType(InventoryEventType.INVENTORY_UPDATED);
        event.setProductId(request.getProductId());
        event.setQuantity(request.getQuantity());
        eventProducer.sendEvent(event);
    }
}
