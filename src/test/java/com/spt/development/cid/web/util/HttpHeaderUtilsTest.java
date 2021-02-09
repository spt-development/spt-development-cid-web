package com.spt.development.cid.web.util;

import org.junit.jupiter.api.Test;

import static com.spt.development.cid.web.util.HttpHeaderUtils.sanitizeHeaderValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class HttpHeaderUtilsTest {
    private interface TestData {
        String FIRST_PART = "first part";
        String SECOND_PART = "second part";
        String BOTH_PARTS = FIRST_PART + SECOND_PART;
    }

    @Test
    void sanitizeHeaderValue_stringWithoutNewLineOrCarriageReturn_shouldBeUnchanged() {
        final String result = sanitizeHeaderValue(TestData.BOTH_PARTS);

        assertThat(result, is(TestData.BOTH_PARTS));
    }

    @Test
    void sanitizeHeaderValue_stringWithNewLine_shouldRemoveNewLine() {
        final String result = sanitizeHeaderValue(TestData.FIRST_PART + "\n" + TestData.SECOND_PART);

        assertThat(result, is(TestData.BOTH_PARTS));
    }

    @Test
    void sanitizeHeaderValue_stringWithCarriageReturn_shouldRemoveCarriageReturn() {
        final String result = sanitizeHeaderValue(TestData.FIRST_PART + "\r" + TestData.SECOND_PART);

        assertThat(result, is(TestData.BOTH_PARTS));
    }

    @Test
    void sanitizeHeaderValue_stringWithCarriageReturnAndNewLine_shouldRemoveCarriageReturnAndNewLine() {
        final String result = sanitizeHeaderValue(TestData.FIRST_PART + "\r\n" + TestData.SECOND_PART);

        assertThat(result, is(TestData.BOTH_PARTS));
    }
}