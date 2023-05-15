package config;

import com.google.auth.oauth2.GoogleCredentials;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.options.DataflowPipelineWorkerPoolOptions;
import org.apache.beam.runners.dataflow.options.DataflowWorkerLoggingOptions.Level;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class DataflowConfig {

    private DataflowConfig() {
    }


    public static DataflowPipelineOptions toJobOptions() throws IOException {

        String gcpProject = System.getenv("GCP_PROJECT");
        String dataflowRegion = System.getenv("DATAFLOW_REGION");
        int dataFlowMaxNumWorkers = System.getenv().containsKey("DATAFLOW_MAX_NUM_WORKERS") ? Integer.parseInt(System.getenv("DATAFLOW_MAX_NUM_WORKERS")): 1 ;

        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setProject(gcpProject);
        options.setRegion(dataflowRegion);
        options.setGcpCredential(GoogleCredentials.getApplicationDefault()
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")));
        options.setStreaming(true);
        options.setRunner(DataflowRunner.class);
        options.setAppName("Teqnation-Beam");
        options.setJobName("Teqnation-Beam");
        options.setMaxNumWorkers(dataFlowMaxNumWorkers);
        options.setDefaultWorkerLogLevel(Level.DEBUG);
        options.setAutoscalingAlgorithm(DataflowPipelineWorkerPoolOptions.AutoscalingAlgorithmType.THROUGHPUT_BASED);
        options.setEnableStreamingEngine(true);
        return options;
    }


}
