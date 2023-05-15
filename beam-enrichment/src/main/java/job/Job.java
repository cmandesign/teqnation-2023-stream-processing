package job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Customer;
import model.EnrichedOrderWithCustomerData;
import model.Order;
import model.OrderEnrichmentError;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.schemas.NoSuchSchemaException;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.transforms.Convert;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.JsonToRow;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.transforms.windowing.*;
import org.apache.beam.sdk.values.*;

import java.io.IOException;


public class Job {

    private final PipelineOptions pipelineOptions;

    public Job(PipelineOptions pipelineOptions) {
        this.pipelineOptions = pipelineOptions;
    }

    public static final TupleTag<Order> ORDER_TUPLE_TAG = new TupleTag<>() {
    };
    public static final TupleTag<OrderEnrichmentError> ENRICHMENT_ERROR_TUPLE_TAG = new TupleTag<>() {
    };
    public static final TupleTag<EnrichedOrderWithCustomerData> ENRICHED_ORDER_WITH_CUSTOMER_DATA_TUPLE_TAG = new TupleTag<>() {
    };
    public static final TupleTag<Customer> CUSTOMER_TUPLE_TAG = new TupleTag<>() {
    };

    public PipelineResult execute() {

        Pipeline pipeline = Pipeline.create(pipelineOptions);

        try {


            PCollection<Customer> customersStream = readCustomers(pipeline)
                    .apply("Windowing Customer", myDefaultWindowStrategy());


            PCollection<Order> ordersStream = readOrdersStream(pipeline)
                    .apply("Windowing Order", myDefaultWindowStrategy());

            PCollection<KV<Long, Customer>> kvCustomerIdCustomer = customersStream
                    .apply(WithKeys.of(Customer::getId).withKeyType(TypeDescriptor.of(Long.class)));

            PCollection<KV<Long, Order>> kvCustomerIdOrder = ordersStream
                    .apply(WithKeys.of(Order::getCustomerId).withKeyType(TypeDescriptor.of(Long.class)));


            PCollection<KV<Long, CoGbkResult>> result =
                    KeyedPCollectionTuple.of(CUSTOMER_TUPLE_TAG, kvCustomerIdCustomer).and(ORDER_TUPLE_TAG, kvCustomerIdOrder)
                            .apply(CoGroupByKey.create());

            PCollectionTuple enrichedOrdersAndErrors = result.apply(ParDo.of(new EnrichmentDoFn())
                    .withOutputTags(ENRICHED_ORDER_WITH_CUSTOMER_DATA_TUPLE_TAG, TupleTagList.of(ENRICHMENT_ERROR_TUPLE_TAG)));

            enrichedOrdersAndErrors.get(ENRICHED_ORDER_WITH_CUSTOMER_DATA_TUPLE_TAG)
                    .apply("Map to String", ParDo.of(new MapToString()))
                    .apply("Sending Pub/sub", PubsubIO.writeStrings().to("projects/teqnation-2023-stream-proc/topics/enriched-orders-dataflow"));

            return pipeline.run();

        } catch (NoSuchSchemaException e) {
            throw new RuntimeException(e);
        }
    }

    private static PCollection<Order> readOrdersStream(Pipeline pipeline) throws NoSuchSchemaException {
        Schema orderSchema = pipeline.getSchemaRegistry().getSchema(Order.class);
        return pipeline
                .apply("Read Orders PubSub Messages", PubsubIO.readStrings().fromSubscription("projects/teqnation-2023-stream-proc/subscriptions/orders-dataflow"))
                .apply("Parse Orders JSON to rows", JsonToRow.withSchema(orderSchema))
                .apply("Row to Order", Convert.to(Order.class));
    }

    private static PCollection<Customer> readCustomers(Pipeline pipeline) throws NoSuchSchemaException {
        Schema customerSchema = pipeline.getSchemaRegistry().getSchema(Customer.class);
        return pipeline
                .apply("Read Customers PubSub Messages",
                        PubsubIO.readStrings().fromSubscription("projects/teqnation-2023-stream-proc/subscriptions/customers-dataflow"))
                .apply("Parse Customers JSON to rows", JsonToRow.withSchema(customerSchema))
                .apply("Row to Customer", Convert.to(Customer.class));
    }

    private static <T> Window<T> myDefaultWindowStrategy() {
        return Window.<T>configure()
                .triggering(
                        Repeatedly.forever(
                                AfterFirst.of(AfterPane.elementCountAtLeast(1),
                                        AfterWatermark.pastEndOfWindow())
                        ))
                .discardingFiredPanes();
    }

    public static class MapToString extends DoFn<Object, String> {

        ObjectMapper objectMapper;

        @Setup
        public void setup() {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }

        @ProcessElement
        public void processing(ProcessContext c) throws IOException {

            Object input = c.element();
            String jsonInString = objectMapper.writeValueAsString(input);
            c.output(jsonInString);

        }

    }

}
