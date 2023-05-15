package model;

import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;

import java.util.List;
import java.util.Objects;

@DefaultSchema(JavaFieldSchema.class)
public class Order{
    public final long id;
    public final long customerId;

    public long getCustomerId() {
        return customerId;
    }

    public final String orderDate;
    public final List<OrderItem> items;
    public final String status;

    @SchemaCreate
    public Order(long id, long customerId, String orderDate, List<OrderItem> items, String status) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", orderDate='" + orderDate + '\'' +
                ", items=" + items +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && customerId == order.customerId && Objects.equals(orderDate, order.orderDate) && Objects.equals(items, order.items) && Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, orderDate, items, status);
    }
}


