package dev.dkorez.msathesis.catalog.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import dev.dkorez.msathesis.catalog.document.InventoryDocument;
import dev.dkorez.msathesis.catalog.document.TransactionDocument;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;

@ApplicationScoped
public class InventoryRepository {
    private final MongoClient mongoClient;

    @Inject
    public InventoryRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public InventoryDocument findByProductId(Long productId) {
        return getCollection().find(eq("product_id", productId)).first();
    }

    public void save(InventoryDocument inventory) {
        getCollection().insertOne(inventory);
    }

    public void update(InventoryDocument inventory) {
        getCollection().replaceOne(eq("_id", inventory.get_id()), inventory);
    }

    public void addTransaction(Long productId, TransactionDocument transaction) {
        getCollection().updateOne(
                eq("product_id", productId),
                push("transactions", transaction)
        );
    }

    public void delete(Long productId) {
        getCollection().deleteMany(eq("product_id", productId));
    }

    private MongoCollection<InventoryDocument> getCollection() {
        return mongoClient.getDatabase("inventory_db").getCollection("inventory", InventoryDocument.class);
    }
}
