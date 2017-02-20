package io.swiftwallet.midgard.core.proxy;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by gibugeorge on 09/02/2017.
 */
@ConfigurationProperties(prefix = "proxy")
public class ProxyServerProperties {

    private boolean bufferRequests;


    public boolean isBufferRequests() {
        return bufferRequests;
    }

    public void setBufferRequests(boolean bufferRequests) {
        this.bufferRequests = bufferRequests;
    }
}
