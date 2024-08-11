package dev.dkorez.msathesis.catalog.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dkorez.msathesis.catalog.model.ReservationRequest;
import dev.dkorez.msathesis.catalog.service.InventoryCoordinator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class InventoryEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final InventoryCoordinator inventoryCoordinator;

    @Inject
    public InventoryEventConsumer(InventoryCoordinator inventoryCoordinator, ObjectMapper objectMapper) {
        this.inventoryCoordinator = inventoryCoordinator;
        this.objectMapper = objectMapper;
    }

    @Incoming("order-events")
    public CompletionStage<Void> processUpdates(String event) {
        try {
            logger.info("incoming order-events: {}", event);
            InventoryEvent inventoryEvent = objectMapper.readValue(event, InventoryEvent.class);

            ReservationRequest inventory = new ReservationRequest();
            inventory.setProductId(inventoryEvent.getProductId());
            inventory.setQuantity(inventoryEvent.getQuantity());
            inventory.setOrderId(inventoryEvent.getOrderId());
            switch (inventoryEvent.getType()) {
                case ORDER_CREATED -> inventoryCoordinator.reserveQuantity(inventory);
                case ORDER_CANCELLED -> inventoryCoordinator.cancelReservation(inventory);
            }
        } catch (JsonProcessingException e) {
            logger.error("error processing event: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
