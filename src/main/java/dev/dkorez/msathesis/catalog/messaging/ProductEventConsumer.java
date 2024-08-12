package dev.dkorez.msathesis.catalog.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dkorez.msathesis.catalog.model.InventoryRequest;
import dev.dkorez.msathesis.catalog.service.InventoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(ProductEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @Inject
    public ProductEventConsumer(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @Incoming("product-events")
    public CompletionStage<Void> processUpdates(String event) {
        try {
            logger.info("incoming order-events: {}", event);
            ProductEvent productEvent = objectMapper.readValue(event, ProductEvent.class);

            InventoryRequest inventory = new InventoryRequest(productEvent.getProductId(), productEvent.getProduct().getQuantity());
            switch (productEvent.getType()) {
                case CREATED, UPDATED -> inventoryService.updateQuantity(inventory);
                case DELETED -> inventoryService.deleteInventory(productEvent.getProductId());
            }
        } catch (JsonProcessingException e) {
            logger.error("error processing product-event: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
