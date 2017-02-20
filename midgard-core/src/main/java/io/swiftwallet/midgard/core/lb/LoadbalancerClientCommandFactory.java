package io.swiftwallet.midgard.core.lb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommandFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

/**
 * Created by gibugeorge on 20/02/2017.
 */
public class LoadbalancerClientCommandFactory extends AbstractRibbonCommandFactory {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private final ZuulProperties zuulProperties;

    public LoadbalancerClientCommandFactory(final Set<ZuulFallbackProvider> fallbackProviders, final ZuulProperties zuulProperties) {
        super(fallbackProviders);
        this.zuulProperties = zuulProperties;
    }

    @Override
    public RibbonCommand create(final RibbonCommandContext context) {
        final ZuulFallbackProvider zuulFallbackProvider = getFallbackProvider(context.getServiceId());
        final String serviceId = context.getServiceId();
        final RibbonLoadBalancingHttpClient client = applicationContext.getBean(serviceId, RibbonLoadBalancingHttpClient.class);
        return new HttpClientRibbonCommand(serviceId, client, context, zuulProperties, zuulFallbackProvider);
    }
}
