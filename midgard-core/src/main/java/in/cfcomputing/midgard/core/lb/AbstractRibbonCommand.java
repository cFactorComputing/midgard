package in.cfcomputing.midgard.core.lb;

import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.ClientRequest;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.http.HttpResponse;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.zuul.context.RequestContext;
import in.cfcomputing.midgard.core.proxy.ProxyServerProperties;
import org.springframework.cloud.netflix.ribbon.RibbonHttpResponse;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.client.ClientHttpResponse;

/**
 * This is just an overridden file of org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommand
 * to configure executionTimeoutEnabled Hystrix Command Property
 *
 * @see org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommand
 */
public abstract class AbstractRibbonCommand<LBC extends AbstractLoadBalancerAwareClient<RQ, RS>, RQ extends ClientRequest, RS extends HttpResponse>
        extends HystrixCommand<ClientHttpResponse> implements RibbonCommand {

    protected LBC client;
    protected RibbonCommandContext context;
    protected ZuulFallbackProvider zuulFallbackProvider;
    protected IClientConfig config;

    public AbstractRibbonCommand(LBC client, RibbonCommandContext context,
                                 ProxyServerProperties zuulProperties) {
        this("default", client, context, zuulProperties);
    }

    public AbstractRibbonCommand(String commandKey, LBC client,
                                 RibbonCommandContext context, ProxyServerProperties zuulProperties) {
        this(commandKey, client, context, zuulProperties, null);
    }

    public AbstractRibbonCommand(String commandKey, LBC client,
                                 RibbonCommandContext context, ProxyServerProperties zuulProperties,
                                 ZuulFallbackProvider fallbackProvider) {
        this(commandKey, client, context, zuulProperties, fallbackProvider, null);
    }

    public AbstractRibbonCommand(String commandKey, LBC client,
                                 RibbonCommandContext context, ProxyServerProperties zuulProperties,
                                 ZuulFallbackProvider fallbackProvider, IClientConfig config) {
        super(getSetter(commandKey, zuulProperties));
        this.client = client;
        this.context = context;
        this.zuulFallbackProvider = fallbackProvider;
        this.config = config;
    }

    protected static Setter getSetter(final String commandKey,
                                      ProxyServerProperties zuulProperties) {

        // @formatter:off
        final HystrixCommandProperties.Setter setter = HystrixCommandProperties.Setter();

        setter.withExecutionTimeoutEnabled(zuulProperties.isExecutionTimeoutEnabled());
        setter.withCircuitBreakerEnabled(false);

        return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RibbonCommand"))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
                .andCommandPropertiesDefaults(setter);
        // @formatter:on
    }

    @Override
    protected ClientHttpResponse run() throws Exception {
        final RequestContext currentContext = RequestContext.getCurrentContext();

        RQ request = createRequest();
        RS response = this.client.executeWithLoadBalancer(request, config);

        currentContext.set("ribbonResponse", response);

        // Explicitly close the HttpResponse if the Hystrix command timed out to
        // release the underlying HTTP connection held by the response.
        //
        if (this.isResponseTimedOut()) {
            if (response != null) {
                response.close();
            }
        }

        return new RibbonHttpResponse(response);
    }

    @Override
    protected ClientHttpResponse getFallback() {
        if (zuulFallbackProvider != null) {
            return zuulFallbackProvider.fallbackResponse();
        }
        return super.getFallback();
    }

    public LBC getClient() {
        return client;
    }

    public RibbonCommandContext getContext() {
        return context;
    }

    protected abstract RQ createRequest() throws Exception;

    public void setClient(LBC client) {
        this.client = client;
    }
}