package com.spt.development.cid.web.filter;

import com.spt.development.cid.CorrelationId;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;

import java.io.IOException;

/**
 * Servlet filter for setting correlationID in {@link MDC} context. Filter must be configured to be applied after
 * {@link CorrelationIdFilter}.
 */
public class MdcCorrelationIdFilter implements Filter {
    /**
     * Default MDC correlation ID key, overridden with constructor.
     */
    public static final String MDC_CID_KEY = "cid";

    private final String cidKey;

    /**
     * Creates default filter that uses MDC_CID_KEY for the correlation ID key.
     */
    public MdcCorrelationIdFilter() {
        this(MDC_CID_KEY);
    }

    /**
     * Creates filter with a custom correlation ID MDC key.
     *
     * @param cidKey the custom name of the correlation ID key.
     */
    public MdcCorrelationIdFilter(final String cidKey) {
        this.cidKey = cidKey;
    }

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        try (MDC.MDCCloseable mdc = MDC.putCloseable(cidKey, CorrelationId.get())) {
            assert mdc != null;

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
