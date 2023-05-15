package job;

import model.Customer;
import model.EnrichedOrderWithCustomerData;
import model.Order;
import model.OrderEnrichmentError;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.state.*;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Duration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static job.Job.*;

public class EnrichmentDoFn extends DoFn<KV<Long, CoGbkResult>, EnrichedOrderWithCustomerData> {

    private final String CUSTOMER_STATE_ID = "customer";
    private final String CUSTOMER_STATE_EXPIRY_TIMER_ID = "customerExpiryTimer";
    @StateId(CUSTOMER_STATE_ID)
    private final StateSpec<ValueState<Customer>> customerStateSpec = StateSpecs.value(SerializableCoder.of(Customer.class));

    @TimerId(CUSTOMER_STATE_EXPIRY_TIMER_ID)
    private final TimerSpec expirySpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

    @ProcessElement
    public void processElement(ProcessContext processContext,
                               @StateId(CUSTOMER_STATE_ID) ValueState<Customer> customerState,
                               @TimerId(CUSTOMER_STATE_EXPIRY_TIMER_ID) Timer expiryTimer) {

        KV<Long, CoGbkResult> e = processContext.element();

        CoGbkResult result = Objects.requireNonNull(e.getValue());

        List<Customer> customers = StreamSupport.stream(Objects.requireNonNull(result.getAll(CUSTOMER_TUPLE_TAG)).spliterator(), false).toList();

        List<Order> orders = StreamSupport.stream(Objects.requireNonNull(result.getAll(ORDER_TUPLE_TAG)).spliterator(), false).toList();

        updateCustomerInState(customers, customerState, expiryTimer);

        process(orders, processContext, customerState);

    }


    private void updateCustomerInState(List<Customer> customers, ValueState<Customer> customerState, Timer expiryTimer) {
        customers.stream()
                .findAny()
                .ifPresent(customer -> {
                    customerState.write(customer);
                    setTimer(expiryTimer);
                });

    }

    private void setTimer(Timer expiryTimer) {
        expiryTimer.offset(Duration.standardDays(365L)).setRelative();
    }

    private void process(List<Order> orders, ProcessContext processContext, ValueState<Customer> customerState) {
        Optional<Customer> customer = Optional.ofNullable(customerState.read());
        if (customer.isEmpty()) {
            // Error handling
            processContext.output(ENRICHMENT_ERROR_TUPLE_TAG,
                    OrderEnrichmentError.create(
                            "Customer missing in state",
                            orders.stream().findFirst().get())
            );
            return;
        }
        orders.stream()
                .map(order -> EnrichedOrderWithCustomerData.create(customerState.read(), order))
                .forEach(processContext::output);

    }

    @OnTimer(CUSTOMER_STATE_EXPIRY_TIMER_ID)
    public void clearState(@StateId(CUSTOMER_STATE_ID) ValueState<Customer> customerState) {
        customerState.clear();
    }


}
