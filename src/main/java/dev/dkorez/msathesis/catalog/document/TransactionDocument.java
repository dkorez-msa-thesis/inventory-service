package dev.dkorez.msathesis.catalog.document;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class TransactionDocument {
    private ObjectId _id;
    @BsonProperty("product_id")
    private Long productId;
    private Integer quantity;
    private TransactionType type;
    @BsonProperty("order_id")
    private Long orderId;
}
