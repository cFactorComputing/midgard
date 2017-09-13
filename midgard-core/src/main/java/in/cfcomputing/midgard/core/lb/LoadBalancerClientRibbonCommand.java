package in.cfcomputing.midgard.core.lb;

import in.cfcomputing.midgard.core.proxy.ProxyServerProperties;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpRequest;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpResponse;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;

public class LoadBalancerClientRibbonCommand extends AbstractRibbonCommand<RibbonLoadBalancingHttpClient, RibbonApacheHttpRequest, RibbonApacheHttpResponse> {

    public LoadBalancerClientRibbonCommand(String commandKey, RibbonLoadBalancingHttpClient client, RibbonCommandContext context, ProxyServerProperties zuulProperties, ZuulFallbackProvider zuulFallbackProvider) {
        super(commandKey, client, context, zuulProperties, zuulFallbackProvider);
    }

    @Override
    protected RibbonApacheHttpRequest createRequest() throws Exception {
        return new RibbonApacheHttpRequest(this.context);
    }
}
