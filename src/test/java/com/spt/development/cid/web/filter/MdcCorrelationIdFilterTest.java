package com.spt.development.cid.web.filter;

import com.spt.development.cid.CorrelationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.spt.development.cid.web.filter.MdcCorrelationIdFilter.MDC_CID_KEY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MdcCorrelationIdFilterTest {
    private interface TestData {
        String CORRELATION_ID = "30fc1a7c-8fcf-4c10-bc59-094cee91c386";

        String ALT_MDC_CID_KEY = "test-correlation-id";
    }

    @BeforeEach
    void setUp() {
        CorrelationId.set(TestData.CORRELATION_ID);
    }

    @Test
    void doFilter_defaultFilterAnyRequest_shouldAddCorrelationIdToMdcContext() throws Exception {
        createDefaultFilter().doFilter(
                Mockito.mock(HttpServletRequest.class),
                Mockito.mock(HttpServletResponse.class),
                (servletRequest, servletResponse) -> assertThat(MDC.get(MDC_CID_KEY), is(TestData.CORRELATION_ID))
        );

        assertThat(MDC.get(MDC_CID_KEY), is(nullValue()));
    }

    @Test
    void doFilter_defaultFilterAnyRequest_shouldCallNextFilterInChain() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        final FilterChain filterChain = Mockito.mock(FilterChain.class);

        createDefaultFilter().doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_customCidKeyAnyRequest_shouldAddCorrelationIdToMdcContext() throws Exception {
        createFilter().doFilter(
                Mockito.mock(HttpServletRequest.class),
                Mockito.mock(HttpServletResponse.class),
                (servletRequest, servletResponse) ->
                        assertThat(MDC.get(TestData.ALT_MDC_CID_KEY), is(TestData.CORRELATION_ID))
        );

        assertThat(MDC.get(MDC_CID_KEY), is(nullValue()));
    }

    @Test
    void destroy_happyPath_doesNothing() {
        // Just added for coverage :O(
        createDefaultFilter().destroy();
    }

    private MdcCorrelationIdFilter createDefaultFilter() {
        final MdcCorrelationIdFilter filter = new MdcCorrelationIdFilter();
        filter.init(Mockito.mock(FilterConfig.class));

        return filter;
    }

    private MdcCorrelationIdFilter createFilter() {
        final MdcCorrelationIdFilter filter = new MdcCorrelationIdFilter(TestData.ALT_MDC_CID_KEY);
        filter.init(Mockito.mock(FilterConfig.class));

        return filter;
    }
}