package config;

import org.apache.beam.sdk.options.PipelineOptions;

import java.io.IOException;

public class Config {

    private static final String ENVIRONMENT = System.getenv().containsKey("ENVIRONMENT") ? System.getenv("ENVIRONMENT") : "dev";

    private Config() {
    }

    public static PipelineOptions toJobOptions() throws IOException {
        PipelineOptions options = switch (ENVIRONMENT) {
            case "dev" -> DirectRunnerConfig.toJobOptions();
            case "pro" -> DataflowConfig.toJobOptions();
            default -> throw new RuntimeException("Running ENV must be provided");
        };

        return options;
    }

}
