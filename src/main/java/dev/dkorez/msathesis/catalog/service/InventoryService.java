package dev.dkorez.msathesis.catalog.service;

import dev.dkorez.msathesis.catalog.document.InventoryDocument;
import dev.dkorez.msathesis.catalog.document.TransactionDocument;
import dev.dkorez.msathesis.catalog.document.TransactionType;
import dev.dkorez.msathesis.catalog.model.InventoryRequest;
import dev.dkorez.msathesis.catalog.model.ReservationRequest;
import dev.dkorez.msathesis.catalog.repository.InventoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Inject
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void reserveQuantity(ReservationRequest request) {
        InventoryDocument inventory = inventoryRepository.findByProductId(request.getProductId());
        if (inventory == null) {
            throw new NotFoundException("Product not found");
        }
        if (inventory.getQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient quantity");
        }

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());

        TransactionDocument transaction = new TransactionDocument();
        transaction.setProductId(request.getProductId());
        transaction.setQuantity(request.getQuantity());
        transaction.setType(TransactionType.RESERVE);
        transaction.setOrderId(request.getOrderId());

        inventoryRepository.update(inventory);
        inventoryRepository.addTransaction(request.getProductId(), transaction);
    }

    @Transactional
    public void cancelReservation(ReservationRequest request) {
        InventoryDocument inventory = inventoryRepository.findByProductId(request.getProductId());
        if (inventory == null) {
            throw new NotFoundException("Product not found");
        }

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());

        TransactionDocument transaction = new TransactionDocument();
        transaction.setProductId(request.getProductId());
        transaction.setQuantity(request.getQuantity());
        transaction.setType(TransactionType.CANCEL);
        transaction.setOrderId(request.getOrderId());

        inventoryRepository.update(inventory);
        inventoryRepository.addTransaction(request.getProductId(), transaction);
    }

    @Transactional
    public void updateQuantity(InventoryRequest request) {
        InventoryDocument inventory = inventoryRepository.findByProductId(request.getProductId());
        if (inventory == null) {
            inventory = new InventoryDocument();
            inventory.setProductId(request.getProductId());
            inventory.setQuantity(request.getQuantity());
            inventory.setReservedQuantity(0);
            inventoryRepository.save(inventory);
        } else {
            // don't update if the request quantity is the same
            if (inventory.getQuantity().equals(request.getQuantity())) {
                return;
            }

            inventory.setQuantity(request.getQuantity());
            inventoryRepository.update(inventory);
        }

        TransactionDocument transaction = new TransactionDocument();
        transaction.setProductId(request.getProductId());
        transaction.setQuantity(request.getQuantity());
        transaction.setType(TransactionType.UPDATE);

        inventoryRepository.addTransaction(request.getProductId(), transaction);
    }

    @Transactional
    public void deleteInventory(Long productId) {
        inventoryRepository.delete(productId);
    }
}
