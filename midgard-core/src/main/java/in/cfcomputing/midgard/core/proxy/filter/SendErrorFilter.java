package in.cfcomputing.midgard.core.proxy.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by gibugeorge on 29/03/2017.
 */
public class SendErrorFilter extends ZuulFilter {

    protected static final String SEND_ERROR_FILTER_RAN = "sendErrorFilter.ran";
    private static final Logger LOGGER = LoggerFactory.getLogger(SendErrorFilter.class);


    private final SendResponseFilter sendResponseFilter;

    public SendErrorFilter(final SendResponseFilter sendResponseFilter) {
        this.sendResponseFilter = sendResponseFilter;
    }


    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        // only forward to errorPath if it hasn't been forwarded to already
        return ctx.getThrowable() != null
                && !ctx.getBoolean(SEND_ERROR_FILTER_RAN, false);
    }

    @Override
    public Object run() {
        try {
            final RequestContext ctx = RequestContext.getCurrentContext();
            final ZuulException exception = findZuulException(ctx.getThrowable());
            final HttpServletRequest request = ctx.getRequest();
            final HttpServletResponse response = ctx.getResponse();
            if (response == null || response.getOutputStream() == null) {
                return null;
            }
            response.setStatus(exception.nStatusCode);

            request.setAttribute("javax.servlet.error.status_code", exception.nStatusCode);

            LOGGER.warn("Error during filtering", exception);
            request.setAttribute("javax.servlet.error.exception", exception);

            if (StringUtils.hasText(exception.errorCause)) {
                request.setAttribute("javax.servlet.error.message", exception.errorCause);
            }

            sendResponseFilter.run();
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    ZuulException findZuulException(final Throwable throwable) {
        if (throwable.getCause() instanceof ZuulRuntimeException) {
            // this was a failure initiated by one of the local filters
            return (ZuulException) throwable.getCause().getCause();
        }

        if (throwable.getCause() instanceof ZuulException) {
            // wrapped zuul exception
            return (ZuulException) throwable.getCause();
        }

        if (throwable instanceof ZuulException) {
            // exception thrown by zuul lifecycle
            return (ZuulException) throwable;
        }

        // fallback, should never get here
        return new ZuulException(throwable, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
    }


}
