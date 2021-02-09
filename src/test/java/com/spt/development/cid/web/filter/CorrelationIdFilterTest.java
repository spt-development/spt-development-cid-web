package com.spt.development.cid.web.filter;

import com.spt.development.cid.CorrelationId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.spt.development.cid.web.filter.CorrelationIdFilter.CID_HEADER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorrelationIdFilterTest {
    private interface TestData {
        String CORRELATION_ID = "b7700d43-35cd-4898-889f-998e9d1af89f";

        String ALT_CORRELATION_ID_HEADER = "correlation-id";
    }

    @Test
    void doFilter_defaultFilterAnyRequest_shouldSetCorrelationId() throws Exception {
        createDefaultFilter().doFilter(
                createRequestWithValidCorrelationIdHeader(),
                Mockito.mock(HttpServletResponse.class),
                Mockito.mock(FilterChain.class)
        );

        assertThat(CorrelationId.get(), is(notNullValue()));
        assertThat(CorrelationId.get(), is(not("no-cid")));
    }

    @Test
    void doFilter_defaultFilterAnyRequest_shouldSetCorrelationIdResponseHeader() throws Exception {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        createDefaultFilter().doFilter(
                createRequestWithValidCorrelationIdHeader(),
                response,
                Mockito.mock(FilterChain.class)
        );

        final ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);

        verify(response, times(1)).setHeader(eq(CID_HEADER), headerCaptor.capture());

        assertThat(headerCaptor.getValue(), is(CorrelationId.get()));
    }

    @Test
    void doFilter_defaultFilterAnyRequest_shouldCallNextFilterInChain() throws Exception {
        final HttpServletRequest request = createRequestWithValidCorrelationIdHeader();
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        final FilterChain filterChain = Mockito.mock(FilterChain.class);

        createDefaultFilter().doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_filterUsingRequestHeaderForCidAndRequestWithoutCorrelationIdHeader_shouldSetCorrelationId() throws Exception {
        createFilterUsingRequestHeaderForCid().doFilter(
                Mockito.mock(HttpServletRequest.class),
                Mockito.mock(HttpServletResponse.class),
                Mockito.mock(FilterChain.class)
        );

        assertThat(CorrelationId.get(), is(notNullValue()));
        assertThat(CorrelationId.get(), is(not("no-cid")));
    }

    @Test
    void doFilter_filterUsingRequestHeaderForCidAndRequestWithoutCorrelationIdHeader_shouldSetCorrelationIdResponseHeader() throws Exception {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        createFilterUsingRequestHeaderForCid().doFilter(
                Mockito.mock(HttpServletRequest.class),
                response,
                Mockito.mock(FilterChain.class)
        );

        final ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);

        verify(response, times(1)).setHeader(eq(TestData.ALT_CORRELATION_ID_HEADER), headerCaptor.capture());

        assertThat(headerCaptor.getValue(), is(CorrelationId.get()));
    }

    @Test
    void doFilter_filterUsingRequestHeaderForCidAndRequestWithValidCorrelationIdHeader_shouldSetCorrelationId() throws Exception {
        createFilterUsingRequestHeaderForCid().doFilter(
                createRequestWithValidCorrelationIdHeader(),
                Mockito.mock(HttpServletResponse.class),
                Mockito.mock(FilterChain.class)
        );

        assertThat(CorrelationId.get(), is(TestData.CORRELATION_ID));
    }

    @Test
    void doFilter_filterUsingRequestHeaderForCidAndRequestWithValidCorrelationIdHeader_shouldSetCorrelationIdResponseHeader() throws Exception {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        createFilterUsingRequestHeaderForCid().doFilter(
                createRequestWithValidCorrelationIdHeader(),
                response,
                Mockito.mock(FilterChain.class)
        );

        verify(response, times(1)).setHeader(TestData.ALT_CORRELATION_ID_HEADER, TestData.CORRELATION_ID);
    }

    private HttpServletRequest createRequestWithValidCorrelationIdHeader() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        when(request.getHeader(TestData.ALT_CORRELATION_ID_HEADER)).thenReturn(TestData.CORRELATION_ID);

        return request;
    }

    @Test
    void destroy_happyPath_doesNothing() {
        // Just added for coverage :O(
        createDefaultFilter().destroy();
    }

    private CorrelationIdFilter createDefaultFilter() {
        final CorrelationIdFilter filter =  new CorrelationIdFilter();
        filter.init(Mockito.mock(FilterConfig.class));

        return filter;
    }

    private CorrelationIdFilter createFilterUsingRequestHeaderForCid() {
        final CorrelationIdFilter filter =  new CorrelationIdFilter(TestData.ALT_CORRELATION_ID_HEADER, true);
        filter.init(Mockito.mock(FilterConfig.class));

        return filter;
    }
}