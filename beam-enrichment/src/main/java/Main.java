import config.Config;
import job.Job;
import org.apache.beam.sdk.PipelineResult;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Job job = new Job(Config.toJobOptions());
        PipelineResult pipelineResult = job.execute();
    }
}
