package in.cfcomputing.midgard.core.lb.config;

import in.cfcomputing.midgard.core.lb.LoadBalancerClientCommandFactory;
import in.cfcomputing.midgard.core.proxy.ProxyServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Created by gibugeorge on 20/02/2017.
 */
@Configuration
@EnableConfigurationProperties(ProxyServerProperties.class)
public class LoadbalancerConfiguration {

    @Autowired
    private ProxyServerProperties proxyServerProperties;

    @Bean
    public LoadBalancerClientCommandFactory loadbalancerClientCommandFactory() {
        return new LoadBalancerClientCommandFactory(Collections.emptySet(), proxyServerProperties);
    }
}
