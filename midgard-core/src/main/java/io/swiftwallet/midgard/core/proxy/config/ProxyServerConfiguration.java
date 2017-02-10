package io.swiftwallet.midgard.core.proxy.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.http.ZuulServlet;
import io.swiftwallet.midgard.core.proxy.ProxyServerProperties;
import io.swiftwallet.midgard.core.proxy.StartProxyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gibugeorge on 10/02/2017.
 */
@Configuration
@EnableConfigurationProperties(ProxyServerProperties.class)
public class ProxyServerConfiguration {

    @Autowired
    private ProxyServerProperties proxyProperties;

    @Bean
    public StartProxyServer startProxyServer() {
        return new StartProxyServer(filters());
    }

    @Bean
    public ServletRegistrationBean proxyServlet() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(new ZuulServlet(),
                "/proxy/*");
        servlet.addInitParameter("buffer-requests", "false");
        return servlet;
    }

    public Map<String, ZuulFilter> filters() {
        return new HashMap<>();
    }
}
