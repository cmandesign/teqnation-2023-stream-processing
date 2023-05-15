package config;

import com.google.auth.oauth2.GoogleCredentials;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import java.io.IOException;
import java.util.List;

public class DirectRunnerConfig {

    private DirectRunnerConfig() {
    }

    public static LocalRunnerOptions toJobOptions() throws IOException {

        String pubSubRootUrl = System.getenv().containsKey("PUBSUB_URL") ? System.getenv("PUBSUB_URL") : "";
        String gcpProject = System.getenv("GCP_PROJECT");

        PipelineOptionsFactory.register(LocalRunnerOptions.class);
        LocalRunnerOptions options = PipelineOptionsFactory.as(LocalRunnerOptions.class);
        options.setProject(gcpProject);
        options.setGcpCredential(GoogleCredentials.getApplicationDefault()
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")));
        options.setStreaming(true);
        options.setRunner(DirectRunner.class);
        options.setAppName("Teqnation-Streamproc");
        options.setJobName("Teqnation-Streamproc");
        if (!pubSubRootUrl.isBlank()) {
            options.setPubsubRootUrl(pubSubRootUrl);
        }

        return options;
    }

}
