package config;

import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubOptions;

public interface LocalRunnerOptions extends DirectOptions, PubsubOptions, GcpOptions {

}
