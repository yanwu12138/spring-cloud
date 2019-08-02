package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

@Slf4j
public final class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final ObjectMapper xssSerializerObjectMapper = makeBaseXssSerializerObjectMapper();
    private static final ObjectMapper servletXssSerializerObjectMapper = makeBaseXssSerializerObjectMapper();

    static {
        // The original two ObjectMappers allow comments.
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        xssSerializerObjectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        xssSerializerObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // The servlet version of the XssSerializerObjectMapper sets inclusion
        // level differently.
        servletXssSerializerObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        servletXssSerializerObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper makeBaseXssSerializerObjectMapper() {
        CharacterEscapes characterEscapes = new CharacterEscapes() {

            private static final long serialVersionUID = -7677406402762106256L;
            private final int[] escapeCodes = CharacterEscapes.standardAsciiEscapesForJSON();

            {
                // To help protect against XSS attacks, escape characters
                // significant to HTML
                escapeCodes['<'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['>'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['&'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['"'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['\''] = CharacterEscapes.ESCAPE_STANDARD;
            }

            @Override
            public int[] getEscapeCodesForAscii() {
                return escapeCodes;
            }

            @Override
            public SerializableString getEscapeSequence(final int ch) {
                return null;
            }
        };

        ObjectMapper mapper = new ObjectMapper();

        mapper.getFactory().setCharacterEscapes(characterEscapes);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        return mapper;
    }

    public static ObjectMapper getServletXssSerializerObjectMapper() {
        return servletXssSerializerObjectMapper;
    }

    private static ObjectMapper getObjectMapper(final boolean decodeForXss) {
        return decodeForXss ? xssSerializerObjectMapper : mapper;
    }

    public static String toJsonStringRaw(final Object obj)
            throws JsonGenerationException, JsonMappingException, IOException {
        return toJsonStringRaw(obj, false);
    }

    public static String toJsonStringRaw(final Object obj, final boolean encodeForXss)
            throws JsonGenerationException, JsonMappingException, IOException {
        Writer sw = new StringWriter();
        getObjectMapper(encodeForXss).writerWithDefaultPrettyPrinter().writeValue(sw, obj);
        return sw.toString();
    }

    public static <T> T toObjectRaw(final String json, final Class<T> valueType)
            throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, valueType);
    }

    public static <T> T toObjectRaw(final String json, final Class<T> valueType, final boolean decodeForXss)
            throws JsonParseException, JsonMappingException, IOException {
        return getObjectMapper(decodeForXss).readValue(json, valueType);
    }

    public static String toJsonString(final Object obj) {
        try {
            return toJsonStringRaw(obj);
        } catch (Exception e) {
            log.error("Failed convert {} to JSON", obj, e);
        }
        return null;
    }

    public static String toCompactJsonString(final Object obj) {
        return toCompactJsonString(obj, false);
    }

    public static String toCompactJsonString(final Object obj, final boolean decodeForXss) {
        StringWriter sw = new StringWriter();
        try {
            getObjectMapper(decodeForXss).writer().writeValue(sw, obj);
            return sw.toString();
        } catch (Exception e) {
            log.error("Failed convert {} to JSON", obj, e);
        }
        return null;
    }

    public static <T> T toObject(final String json, final Class<T> valueType) {
        return toObject(json, valueType, true);
    }

    public static <T> T toObject(final String json, final Class<T> valueType, final boolean decodeForXss) {
        try {
            return toObjectRaw(json, valueType, decodeForXss);
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: {}", json, valueType, e);
        }
        return null;
    }

    public static <T> T toObject(final String json, final Class<T> valueType, final Class<?>... parameterTypes) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructParametricType(valueType, parameterTypes));
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: {}", json, valueType, e);
        }
        return null;
    }

    public static <T> List<T> toObjectList(final String json, final Class<T> valueType) {
        return toObjectList(json, valueType, true);
    }

    public static <T> List<T> toObjectList(final String json, final Class<T> valueType, final boolean decodeForXss) {
        try {
            return toObjectListRaw(json, valueType, decodeForXss);
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: List<{}>", json, valueType, e);
        }
        return null;
    }

    public static <T> List<T> toObjectListRaw(final String json, final Class<T> valueType, final boolean decodeForXss)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = getObjectMapper(decodeForXss);
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }

    private JsonUtil() {
        throw new AssertionError("JsonUtil should never be instantiated");
    }
}