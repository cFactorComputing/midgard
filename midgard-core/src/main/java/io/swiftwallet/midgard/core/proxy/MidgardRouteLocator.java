package io.swiftwallet.midgard.core.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.util.RequestUtils;
import org.springframework.util.StringUtils;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Created by gibugeorge on 18/02/2017.
 */
public class MidgardRouteLocator extends SimpleRouteLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MidgardRouteLocator.class);

    private final String zuulServletPath;
    private final String dispatcherServletPath = "/";

    public MidgardRouteLocator(final String servletPath, final ZuulProperties properties) {
        super(servletPath, properties);
        this.zuulServletPath = servletPath;
    }

    public Route getMatchingRoute(final String path) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Locating route for path {}", path);
        }
        final String adjustedPath = adjustPath(path);
        final String microserviceName = getMicroserviceName(path);
        final String targetPath = adjustedPath.replace("/" + microserviceName, "");
        return new Route(microserviceName, targetPath, microserviceName, "", false, null);
    }

    private String adjustPath(final String path) {
        String adjustedPath = path;

        if (RequestUtils.isDispatcherServletRequest()
                && StringUtils.hasText(this.dispatcherServletPath)) {
            if (!this.dispatcherServletPath.equals("/")) {
                adjustedPath = path.substring(this.dispatcherServletPath.length());
                LOGGER.info("Stripped dispatcherServletPath");
            }
        } else if (RequestUtils.isZuulServletRequest()) {
            if (StringUtils.hasText(this.zuulServletPath)
                    && !this.zuulServletPath.equals("/")) {
                adjustedPath = path.substring(this.zuulServletPath.length());
                LOGGER.info("Stripped zuulServletPath");
            }
        }
        LOGGER.info("adjustedPath=" + path);
        return adjustedPath;
    }

    public String getMicroserviceName(String path) {
        String microserviceName = null;
        if (RequestUtils.isZuulServletRequest()) {
            String adjustedPath = adjustPath(path);
            microserviceName = substringBefore(substringAfter(adjustedPath, "/"), "/");
        }
        return microserviceName;
    }

}
