package in.cfcomputing.midgard.core.proxy.config;

import com.google.common.collect.Maps;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.http.ZuulServlet;
import in.cfcomputing.midgard.core.lb.LoadBalancerClientCommandFactory;
import in.cfcomputing.midgard.core.lb.config.LoadbalancerConfiguration;
import in.cfcomputing.midgard.core.proxy.MidgardRouteLocator;
import in.cfcomputing.midgard.core.proxy.StartProxyServer;
import in.cfcomputing.midgard.core.proxy.filter.SendErrorFilter;
import in.cfcomputing.midgard.security.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.DebugFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.FormBodyWrapperFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.Servlet30WrapperFilter;
import org.springframework.cloud.netflix.zuul.filters.pre.ServletDetectionFilter;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by gibugeorge on 10/02/2017.
 */
@Configuration
@EnableConfigurationProperties(ZuulProperties.class)
@Import({LoadbalancerConfiguration.class})
public class ProxyServerConfiguration {

    @Autowired
    private ZuulProperties proxyProperties;

    @Autowired(required = false)
    private List<RibbonRequestCustomizer> requestCustomizers = Collections.emptyList();

    @Autowired
    private LoadBalancerClientCommandFactory loadBalancerClientCommandFactory;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public StartProxyServer startProxyServer(final Map<String, ZuulFilter> filters) {
        return new StartProxyServer(filters);
    }

    @Bean
    public ServletRegistrationBean proxyServlet() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(new ZuulServlet(),
                "/proxy/*");
        servlet.addInitParameter("buffer-requests", "true");
        return servlet;
    }

    @Bean
    public ProxyRequestHelper proxyRequestHelper() {
        return new ProxyRequestHelper();
    }

    @Bean
    public RibbonRoutingFilter ribbonRoutingFilter() {
        return new RibbonRoutingFilter(proxyRequestHelper(), loadBalancerClientCommandFactory, requestCustomizers);
    }

    @Bean
    public MidgardRouteLocator midgardRouteLocator() {
        return new MidgardRouteLocator("/proxy", proxyProperties);
    }

    @Bean
    public PreDecorationFilter preDecorationFilter() {
        return new PreDecorationFilter(midgardRouteLocator(), "/", proxyProperties, proxyRequestHelper());
    }

    @Bean
    public DebugFilter debugFilter() {
        return new DebugFilter();
    }

    @Bean
    public SendResponseFilter sendResponseFilter() {
        return new SendResponseFilter();
    }

    @Bean
    public SendErrorFilter sendErrorFilter() {
        return new SendErrorFilter(sendResponseFilter());
    }

    @Bean
    public FormBodyWrapperFilter formBodyWrapperFilter() {
        return new FormBodyWrapperFilter();
    }

    @Bean
    public ServletDetectionFilter servletDetectionFilter() {
        return new ServletDetectionFilter();
    }

    @Bean
    public Servlet30WrapperFilter servlet30WrapperFilter() {
        return new Servlet30WrapperFilter();
    }


    @Bean
    public Map<String, ZuulFilter> filters() {
        final Map<String, ZuulFilter> filters = Maps.newHashMap();
        filters.put("auth-filter", authenticationFilter);
        filters.put("load-balanced-routing-filter", ribbonRoutingFilter());
        filters.put("predecoraton-filter", preDecorationFilter());
        filters.put("debug-filter", debugFilter());
        filters.put("send-response-filter", sendResponseFilter());
        filters.put("send-error-filter", sendErrorFilter());
        filters.put("form-body-wrapper-filter", formBodyWrapperFilter());
        filters.put("servlet-detection-filter", servletDetectionFilter());
        filters.put("servlet-3.0-wrapper-filter", servlet30WrapperFilter());
        return filters;
    }

}
