package model;

import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;

import java.util.Objects;

@DefaultSchema(JavaFieldSchema.class)
public class EnrichedOrderWithCustomerData {
    public final Customer customer;
    public final Order order;

    public static EnrichedOrderWithCustomerData create(Customer customer, Order order){
        return new EnrichedOrderWithCustomerData(customer, order);
    }
    @SchemaCreate
    public EnrichedOrderWithCustomerData(Customer customer, Order order) {
        this.customer = customer;
        this.order = order;
    }





    @Override
    public String toString() {
        return "EnrichedOrderWithCustomerData{" +
                "customer=" + customer +
                ", order=" + order +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrichedOrderWithCustomerData that = (EnrichedOrderWithCustomerData) o;
        return Objects.equals(customer, that.customer) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, order);
    }


}
