package com.bixuebihui;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class Base64ConverterTest extends TestCase {

    private static class TestConverter implements Base64Converter<String, String> {
        @Override
        public List<String> toDtoList(List<String> info) {
            return info;
        }

        @Override
        public List<String> toDaoList(List<String> info) {
            return info;
        }
    }

    private final TestConverter converter = new TestConverter();

    public void testEncodeAndDecodeBase64() {
        assertNull(converter.encodeBase64(null));
        assertNull(converter.decodeBase64(null));

        String encoded = converter.encodeBase64("hello");
        assertEquals("hello", converter.decodeBase64(encoded));
        assertEquals("not-base64", converter.decodeBase64("not-base64"));
    }

    public void testListConversion() {
        List<String> source = Arrays.asList("a", "b");
        assertEquals(source, converter.toDtoList(source));
        assertEquals(source, converter.toDaoList(source));
    }
}
