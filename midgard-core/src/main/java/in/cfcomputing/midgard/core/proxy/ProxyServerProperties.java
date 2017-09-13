package in.cfcomputing.midgard.core.proxy;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by gibugeorge on 09/02/2017.
 */
@ConfigurationProperties(prefix = "proxy")
public class ProxyServerProperties {

    private boolean bufferRequests;
    private boolean executionTimeoutEnabled = false;

    public boolean isBufferRequests() {
        return bufferRequests;
    }

    public void setBufferRequests(boolean bufferRequests) {
        this.bufferRequests = bufferRequests;
    }

    public boolean isExecutionTimeoutEnabled() {
        return executionTimeoutEnabled;
    }

    public void setExecutionTimeoutEnabled(boolean executionTimeoutEnabled) {
        this.executionTimeoutEnabled = executionTimeoutEnabled;
    }
}
