package dev.dkorez.msathesis.catalog.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dkorez.msathesis.catalog.model.InventoryRequest;
import dev.dkorez.msathesis.catalog.model.OrderDto;
import dev.dkorez.msathesis.catalog.model.OrderItemDto;
import dev.dkorez.msathesis.catalog.model.ReservationRequest;
import dev.dkorez.msathesis.catalog.service.InventoryCoordinator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class OrderEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final InventoryCoordinator inventoryCoordinator;

    @Inject
    public OrderEventConsumer(InventoryCoordinator inventoryCoordinator, ObjectMapper objectMapper) {
        this.inventoryCoordinator = inventoryCoordinator;
        this.objectMapper = objectMapper;
    }

    @Incoming("order-events")
    public CompletionStage<Void> processUpdates(String event) {
        try {
            logger.info("incoming order-events: {}", event);
            OrderEvent orderEvent = objectMapper.readValue(event, OrderEvent.class);

            OrderDto order = orderEvent.getOrder();
            switch (orderEvent.getType()) {
                case CREATED -> {
                    List<ReservationRequest> reservations = convertToReservations(order);
                    reservations.forEach(inventoryCoordinator::reserveQuantity);
                }
                case CANCELLED -> {
                    List<ReservationRequest> reservations = convertToReservations(order);
                    reservations.forEach(inventoryCoordinator::cancelReservation);
                }
                case UPDATED -> {
                    List<InventoryRequest> inventories = convertToInventoryRequest(order);
                    inventories.forEach(i -> inventoryCoordinator.updateQuantity(i, true));
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("error processing event: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    private List<ReservationRequest> convertToReservations(OrderDto order) {
        List<ReservationRequest> reservations = new ArrayList<>();
        List<OrderItemDto> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            for (OrderItemDto orderItem : items) {
                reservations.add(new ReservationRequest(orderItem.getProductId(), orderItem.getQuantity(), order.getId()));
            }
        }

        return reservations;
    }

    private List<InventoryRequest> convertToInventoryRequest(OrderDto order) {
        List<InventoryRequest> inventories = new ArrayList<>();
        List<OrderItemDto> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            for (OrderItemDto orderItem : items) {
                inventories.add(new InventoryRequest(orderItem.getProductId(), orderItem.getQuantity()));
            }
        }

        return inventories;
    }
}
