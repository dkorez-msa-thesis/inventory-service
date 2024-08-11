package dev.dkorez.msathesis.catalog.document;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class InventoryDocument {
    private ObjectId _id;
    @BsonProperty("product_id")
    private Long productId;
    private Integer quantity;
    @BsonProperty("reserved_quantity")
    private Integer reservedQuantity;
    private List<TransactionDocument> transactions;
}
