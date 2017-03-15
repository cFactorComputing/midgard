package io.swiftwallet.midgard.core.lb;

import io.swiftwallet.midgard.core.proxy.ProxyServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommandFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

/**
 * Created by gibugeorge on 20/02/2017.
 */
public class LoadBalancerClientCommandFactory extends AbstractRibbonCommandFactory {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private final ProxyServerProperties proxyServerProperties;

    public LoadBalancerClientCommandFactory(final Set<ZuulFallbackProvider> fallbackProviders, final ProxyServerProperties proxyServerProperties) {
        super(fallbackProviders);
        this.proxyServerProperties = proxyServerProperties;
    }

    @Override
    public RibbonCommand create(final RibbonCommandContext context) {
        final ZuulFallbackProvider zuulFallbackProvider = getFallbackProvider(context.getServiceId());
        final String serviceId = context.getServiceId();
        final RibbonLoadBalancingHttpClient client = applicationContext.getBean(serviceId, RibbonLoadBalancingHttpClient.class);
        return new LoadBalancerClientRibbonCommand(serviceId, client, context, proxyServerProperties, zuulFallbackProvider);
    }
}
