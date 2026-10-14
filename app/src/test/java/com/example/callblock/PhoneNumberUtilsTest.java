package com.example.callblock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.callblock.utils.PhoneNumberUtils;

import org.junit.Test;

public class PhoneNumberUtilsTest {

    @Test
    public void testNormalizePhoneNumber() {
        assertEquals("02473008899", PhoneNumberUtils.normalize("+84 24 7300 8899"));
        assertEquals("0901234567", PhoneNumberUtils.normalize("+84901234567"));
        assertEquals("0901234567", PhoneNumberUtils.normalize("84901234567"));
        assertEquals("190066", PhoneNumberUtils.normalize("1900 66xx"));
        assertEquals("190066", PhoneNumberUtils.normalize("1900 66**"));
    }

    @Test
    public void testIsPrefixPattern() {
        assertTrue(PhoneNumberUtils.isPrefixPattern("190066xx"));
        assertTrue(PhoneNumberUtils.isPrefixPattern("190066*"));
        assertTrue(PhoneNumberUtils.isPrefixPattern("1900"));
    }

    @Test
    public void testFormatDisplay() {
        assertEquals("024 7300 8899", PhoneNumberUtils.formatDisplay("02473008899"));
        assertEquals("090 123 4567", PhoneNumberUtils.formatDisplay("0901234567"));
    }
}
