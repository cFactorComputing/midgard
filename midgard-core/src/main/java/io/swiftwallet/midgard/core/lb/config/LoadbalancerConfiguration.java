package io.swiftwallet.midgard.core.lb.config;

import io.swiftwallet.midgard.core.lb.LoadbalancerClientCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Created by gibugeorge on 20/02/2017.
 */
@Configuration
@EnableConfigurationProperties(ZuulProperties.class)
public class LoadbalancerConfiguration {

    @Autowired
    private ZuulProperties zuulProperties;

    @Bean
    public LoadbalancerClientCommandFactory loadbalancerClientCommandFactory() {
        return new LoadbalancerClientCommandFactory(Collections.<ZuulFallbackProvider>emptySet(), zuulProperties);
    }
}
