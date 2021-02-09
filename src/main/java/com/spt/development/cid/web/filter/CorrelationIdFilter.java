package com.spt.development.cid.web.filter;

import com.spt.development.cid.CorrelationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.spt.development.cid.web.util.HttpHeaderUtils.sanitizeHeaderValue;

/**
 * Servlet filter for initialising {@link CorrelationId} with a random UUID or optionally from a correlation ID
 * request header.
 *
 * <p>The correlation ID will be returned as a response header.</p>
 */
public class CorrelationIdFilter implements Filter {
    /**
     * Default request/response correlation ID header, overridden with constructor.
     */
    public static final String CID_HEADER = "X-Correlation-ID";

    private static final Logger LOG = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private final String cidHeader;
    private final boolean useRequestHeader;

    /**
     * Creates default filter that uses CID_HEADER for the correlation ID header and does not use the request
     * correlation ID header for initialising the correlation ID.
     */
    public CorrelationIdFilter() {
        this(CID_HEADER);
    }

    /**
     * Creates filter with a custom correlation ID header.
     *
     * @param cidHeader the custom name of the correlation ID header.
     */
    public CorrelationIdFilter(String cidHeader) {
        this(cidHeader, false);
    }

    /**
     * Creates filter with a custom correlation ID header and initialises the correlation ID from the correlation ID
     * request header if it is set.
     *
     * @param cidHeader the custom name of the correlation ID header.
     * @param useRequestHeader a flag to determine whether or not to set the correlation ID from the request header.
     */
    public CorrelationIdFilter(final String cidHeader, final boolean useRequestHeader) {
        this.cidHeader = cidHeader;
        this.useRequestHeader = useRequestHeader;
    }

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        assert servletRequest instanceof HttpServletRequest;
        assert servletResponse instanceof HttpServletResponse;

        initializeCorrelationId((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void initializeCorrelationId(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        CorrelationId.set(getCorrelationIdFromHeader(httpServletRequest).orElse(UUID.randomUUID().toString()));

        httpServletResponse.setHeader(cidHeader, sanitizeHeaderValue(CorrelationId.get()));

        LOG.debug("[{}] Generated new correlationId", CorrelationId.get());
    }

    private Optional<String> getCorrelationIdFromHeader(HttpServletRequest httpServletRequest) {
        if (!useRequestHeader) {
            return Optional.empty();
        }
        return Optional.ofNullable(httpServletRequest.getHeader(cidHeader));
    }

    @Override
    public void destroy() {
    }
}
