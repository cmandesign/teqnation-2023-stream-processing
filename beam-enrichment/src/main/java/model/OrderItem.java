package model;

import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;

import java.util.Objects;

@DefaultSchema(JavaFieldSchema.class)
public class OrderItem{
    public final long id;
    public final long productId;
    public final int quantity;
    public final double price;

    @SchemaCreate
    public OrderItem(long id, long productId, int quantity, double price) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return id == orderItem.id && productId == orderItem.productId && quantity == orderItem.quantity && Double.compare(orderItem.price, price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, quantity, price);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}