package model;

import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;

import java.util.Objects;

@DefaultSchema(JavaFieldSchema.class)
public class OrderEnrichmentError {
    public final String error;
    public final Order order;

    public static OrderEnrichmentError create(String error, Order order){
        return new OrderEnrichmentError(error, order);
    }

    @SchemaCreate
    public OrderEnrichmentError(String error, Order order) {
        this.error = error;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEnrichmentError that = (OrderEnrichmentError) o;
        return Objects.equals(error, that.error) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, order);
    }

    @Override
    public String toString() {
        return "OrderEnrichmentError{" +
                "error='" + error + '\'' +
                ", order=" + order +
                '}';
    }
}
