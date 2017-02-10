package io.swiftwallet.midgard.core.proxy;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.monitoring.MonitoringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * Created by gibugeorge on 10/02/2017.
 */
public class StartProxyServer implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartProxyServer.class);

    private final Map<String, ZuulFilter> filters;

    public StartProxyServer(final Map<String, ZuulFilter> filters) {
        this.filters = filters;

    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting proxy server");
        }
        MonitoringHelper.initMocks();
        final FilterRegistry filterRegistry = FilterRegistry.instance();
        filters.forEach((name, filter) -> filterRegistry.put(name, filter));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Stopping proxy server");
        }
        FilterRegistry registry = FilterRegistry.instance();
        filters.forEach((name, filter) -> registry.remove(name));
    }
}
